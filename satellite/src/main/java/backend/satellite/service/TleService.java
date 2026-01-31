package backend.satellite.service;

import backend.satellite.exception.ExternalApiException;
import backend.satellite.exception.TleDataNotFoundException;
import backend.satellite.model.TleData;
import backend.satellite.repository.TleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TleService {

    private static final Logger logger = LoggerFactory.getLogger(TleService.class);
    private static final String CELESTRAK_URL = "https://celestrak.org/NORAD/elements/gp.php?CATNR=";
    private static final int CACHE_HOURS = 5;

    @Autowired
    private TleRepository tleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public TleData getTleData(String satNumber) {
        logger.debug("Fetching TLE data for satellite: {}", satNumber);
        
        TleData tleData = tleRepository.findBySatNumber(satNumber);

        if (tleData == null || isDataStale(tleData)) {
            logger.info("TLE data not found or stale for satellite: {}. Fetching from Celestrak.", satNumber);
            
            int existingFetchCount = (tleData != null) ? tleData.getFetchCount() : 0;
            
            if (tleData != null) {
                tleRepository.deleteBySatNumber(satNumber);
            }

            tleData = fetchTleDataFromCelestrak(satNumber);
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
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    private TleData fetchTleDataFromCelestrak(String satNumber) {
        try {
            logger.info("Calling Celestrak API for satellite: {}", satNumber);
            String tleString = restTemplate.getForObject(CELESTRAK_URL + satNumber, String.class);

            if (tleString == null || tleString.trim().isEmpty()) {
                throw new TleDataNotFoundException("No TLE data found for satellite: " + satNumber);
            }

            TleData tleData = new TleData();
            tleData.setSatNumber(satNumber);
            tleData.setTleString(tleString);
            tleData.setLastUpdated(LocalDateTime.now());

            logger.info("Successfully fetched TLE data for satellite: {}", satNumber);
            return tleData;
        } catch (RestClientException e) {
            logger.error("Error fetching TLE data from Celestrak for satellite: {}", satNumber, e);
            throw new ExternalApiException("Failed to fetch TLE data from Celestrak for satellite: " + satNumber, e);
        }
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