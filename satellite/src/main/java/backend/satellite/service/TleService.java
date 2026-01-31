package backend.satellite.service;

import backend.satellite.exception.ExternalApiException;
import backend.satellite.exception.TleDataNotFoundException;
import backend.satellite.model.TleData;
import backend.satellite.repository.TleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TleService {

    private static final Logger logger = LoggerFactory.getLogger(TleService.class);
    private static final String SPACETRACK_AUTH_URL = "https://www.space-track.org/ajaxauth/login";
    private static final String SPACETRACK_TLE_URL = "https://www.space-track.org/basicspacedata/query/class/gp/NORAD_CAT_ID/%s/orderby/EPOCH%%20desc/limit/1/format/tle";
    private static final int CACHE_HOURS = 5;

    @Value("${spacetrack.username:}")
    private String spacetrackUsername;

    @Value("${spacetrack.password:}")
    private String spacetrackPassword;

    @Autowired
    private TleRepository tleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public TleData getTleData(String satNumber) {
        logger.debug("Fetching TLE data for satellite: {}", satNumber);
        
        TleData tleData = tleRepository.findBySatNumber(satNumber);

        if (tleData == null || isDataStale(tleData)) {
            logger.info("TLE data not found or stale for satellite: {}. Fetching from Space-Track.", satNumber);
            
            int existingFetchCount = (tleData != null) ? tleData.getFetchCount() : 0;
            
            if (tleData != null) {
                tleRepository.deleteBySatNumber(satNumber);
            }

            tleData = fetchTleDataFromSpaceTrack(satNumber);
            tleData.setFetchCount(existingFetchCount + 1);
            tleRepository.save(tleData);
        } else {
            logger.debug("Using cached TLE data for satellite: {}", satNumber);
            tleData.setFetchCount(tleData.getFetchCount() + 1);
            tleRepository.save(tleData);
        }

        return tleData;
    }

    private boolean isDataStale(TleData tleData) {
        return ChronoUnit.HOURS.between(tleData.getLastUpdated(), LocalDateTime.now()) > CACHE_HOURS;
    }

    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    private TleData fetchTleDataFromSpaceTrack(String satNumber) {
        try {
            // Authenticate and get session cookie
            logger.info("Authenticating with Space-Track for satellite: {}", satNumber);
            String cookie = authenticateSpaceTrack();
            
            // Fetch TLE data using the cookie
            String url = String.format(SPACETRACK_TLE_URL, satNumber);
            logger.info("Fetching TLE data from Space-Track: {}", satNumber);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookie);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String tleString = response.getBody();

            if (tleString == null || tleString.trim().isEmpty()) {
                throw new TleDataNotFoundException("No TLE data found for satellite: " + satNumber);
            }

            TleData tleData = new TleData();
            tleData.setSatNumber(satNumber);
            tleData.setTleString(tleString);
            tleData.setLastUpdated(LocalDateTime.now());

            logger.info("Successfully fetched TLE data for satellite: {} from Space-Track", satNumber);
            return tleData;
        } catch (RestClientException e) {
            logger.warn("Attempt failed to fetch TLE data from Space-Track for satellite: {} - {}", satNumber, e.getMessage());
            throw e; // Let @Retryable handle the retry
        }
    }

    private String authenticateSpaceTrack() {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("identity", spacetrackUsername);
            formData.add("password", spacetrackPassword);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(SPACETRACK_AUTH_URL, request, String.class);
            
            // Extract cookie from response
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null && !cookies.isEmpty()) {
                String cookie = cookies.get(0);
                logger.debug("Space-Track authentication successful");
                return cookie;
            }
            
            throw new ExternalApiException("Failed to authenticate with Space-Track: No cookie received");
        } catch (Exception e) {
            logger.error("Space-Track authentication failed: {}", e.getMessage());
            throw new ExternalApiException("Failed to authenticate with Space-Track", e);
        }
    }

    @org.springframework.retry.annotation.Recover
    public TleData recoverFromSpaceTrackFailure(RestClientException e, String satNumber) {
        logger.error("All retry attempts exhausted for satellite: {}. Error: {}", satNumber, e.getMessage());
        throw new ExternalApiException("Failed to fetch TLE data from Space-Track after multiple retries for satellite: " + satNumber, e);
    }

    @Transactional(readOnly = true)
    public TleData getMostFetchedTleData() {
        logger.debug("Fetching most requested satellite data");
        TleData mostFetched = tleRepository.findMostFetched();
        
        if (mostFetched == null) {
            throw new TleDataNotFoundException("No TLE data available in the database");
        }
        
        return mostFetched;
    }

    @Transactional(readOnly = true)
    public List<TleData> getAllTleData() {
        logger.debug("Fetching all TLE data");
        return tleRepository.findAll();
    }

    @Transactional
    public void deleteTleData(Long id) {
        logger.info("Deleting TLE data with ID: {}", id);
        if (!tleRepository.existsById(id)) {
            throw new TleDataNotFoundException("TLE data not found with ID: " + id);
        }
        tleRepository.deleteById(id);
    }
}