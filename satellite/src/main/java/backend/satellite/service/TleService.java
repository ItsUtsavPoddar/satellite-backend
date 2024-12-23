package backend.satellite.service;

import backend.satellite.model.TleData;
import backend.satellite.repository.TleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TleService {

    @Autowired
    private TleRepository tleRepository;

    private static final String CELESTREK_URL = "https://celestrak.com/NORAD/elements/gp.php?CATNR=";

    public TleData getTleData(String satNumber) {
        TleData tleData = tleRepository.findBySatNumber(satNumber);

        if (tleData == null || ChronoUnit.HOURS.between(tleData.getLastUpdated(), LocalDateTime.now()) > 5) {
            int fetchCount = 0;
            // Preserve fetch count if the record exists
            if (tleData != null) {
                fetchCount = tleData.getFetchCount();
                tleRepository.deleteBySatNumber(satNumber);
            }

            // Fetch new TLE data
            tleData = fetchTleDataFromCelestrek(satNumber);
            tleData.setFetchCount(fetchCount + 1); // Initialize or increment fetch count
            tleRepository.save(tleData);
        } else {
            // Increment fetch count
            tleData.setFetchCount(tleData.getFetchCount() + 1);
            tleRepository.save(tleData);
        }

        return tleData;
    }

    public TleData addTleData(TleData tleData) {
        tleData.setLastUpdated(LocalDateTime.now());
        tleData.setFetchCount(0); // Initialize fetch count
        return tleRepository.save(tleData);
    }

    public TleData updateTleData(Long id, TleData tleData) {
        TleData existingTleData = tleRepository.findById(id).orElseThrow(() -> new RuntimeException("TLE data not found"));
        existingTleData.setSatNumber(tleData.getSatNumber());
        existingTleData.setTleString(tleData.getTleString());
        existingTleData.setLastUpdated(LocalDateTime.now());
        return tleRepository.save(existingTleData);
    }

    public void deleteTleData(Long id) {
        tleRepository.deleteById(id);
    }

     public TleData getMostFetchedTleData() {
        TleData mostFetched = tleRepository.findMostFetched();
        return mostFetched;
    }

    private TleData fetchTleDataFromCelestrek(String satNumber) {
        RestTemplate restTemplate = new RestTemplate();
        String tleString = restTemplate.getForObject(CELESTREK_URL + satNumber, String.class);

        TleData tleData = new TleData();
        tleData.setSatNumber(satNumber);
        tleData.setTleString(tleString);
        tleData.setLastUpdated(LocalDateTime.now());

        return tleData;
    }

    public List<TleData> getAllTleData() {
        return tleRepository.findAll();
    }
}