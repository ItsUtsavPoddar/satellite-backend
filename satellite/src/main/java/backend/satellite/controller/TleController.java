package backend.satellite.controller;

import backend.satellite.model.TleData;
import backend.satellite.service.TleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class TleController {

    private static final Logger logger = LoggerFactory.getLogger(TleController.class);

    @Autowired
    private TleService tleService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> defaultRoute() {
        return ResponseEntity.ok(Map.of(
            "message", "Welcome to the Satellite TLE Data API",
            "version", "2.0",
            "endpoints", "/api/{satNumber}, /api/most-fetched, /api/all"
        ));
    }

    @GetMapping("/health/celestrak")
    public ResponseEntity<Map<String, Object>> checkCelestrakConnectivity() {
        logger.info("Testing Celestrak connectivity");
        Map<String, Object> response = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            String testUrl = "https://celestrak.org/NORAD/elements/gp.php?CATNR=25544&FORMAT=TLE";
            String result = restTemplate.getForObject(testUrl, String.class);
            long duration = System.currentTimeMillis() - startTime;
            
            response.put("status", "SUCCESS");
            response.put("duration_ms", duration);
            response.put("celestrak_reachable", true);
            response.put("response_length", result != null ? result.length() : 0);
            
            logger.info("Celestrak connectivity test successful in {}ms", duration);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - System.currentTimeMillis();
            response.put("status", "FAILED");
            response.put("celestrak_reachable", false);
            response.put("error", e.getClass().getSimpleName());
            response.put("message", e.getMessage());
            
            logger.error("Celestrak connectivity test failed: {}", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }

    @GetMapping("/{satNumber}")
    public ResponseEntity<TleData> getTleData(@PathVariable String satNumber) {
        logger.info("Received request for satellite number: {}", satNumber);
        
        if (!isValidSatelliteNumber(satNumber)) {
            throw new IllegalArgumentException("Invalid satellite number: " + satNumber + ". Must be a positive integer.");
        }
        
        TleData data = tleService.getTleData(satNumber);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/most-fetched")
    public ResponseEntity<TleData> getMostFetchedTleData() {
        logger.info("Received request for most fetched satellite data");
        TleData data = tleService.getMostFetchedTleData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TleData>> getAllTleData() {
        logger.info("Received request for all satellite data");
        List<TleData> data = tleService.getAllTleData();
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTleData(@PathVariable Long id) {
        logger.info("Received request to delete TLE data with ID: {}", id);
        tleService.deleteTleData(id);
        return ResponseEntity.ok(Map.of("message", "TLE data deleted successfully", "id", id.toString()));
    }

    private boolean isValidSatelliteNumber(String satNumber) {
        try {
            int number = Integer.parseInt(satNumber);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}