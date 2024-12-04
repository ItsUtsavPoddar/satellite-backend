package backend.satellite.service;

import backend.satellite.model.TleData;
import backend.satellite.repository.TleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TleService {

    @Autowired
    private TleRepository tleRepository;

    private static final String CELESTREK_URL = "https://celestrak.com/NORAD/elements/gp.php?CATNR=";

    public TleData getTleData(String satNumber) {
        TleData tleData = tleRepository.findBySatNumber(satNumber);
        
        if (tleData == null || ChronoUnit.SECONDS.between(tleData.getLastUpdated(), LocalDateTime.now()) > 15) {

            tleRepository.deleteBySatNumber(satNumber);
            tleData = fetchTleDataFromCelestrek(satNumber);
            tleRepository.save(tleData);
        }

        return tleData;
    }

    private TleData fetchTleDataFromCelestrek(String satNumber) {
        RestTemplate restTemplate = new RestTemplate();
        String tleString = restTemplate.getForObject(CELESTREK_URL + satNumber, String.class);

        // Parse the TLE string and create a TleData object
        // This is a simplified example, you might need to parse the TLE string properly
        TleData tleData = new TleData();
        tleData.setSatNumber(satNumber);
        tleData.setTleString(tleString);
        tleData.setLastUpdated(LocalDateTime.now());

        return tleData;
    }
}