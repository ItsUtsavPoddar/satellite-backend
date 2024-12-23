package backend.satellite.controller;

import backend.satellite.model.TleData;
import backend.satellite.service.TleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TleController {

    @Autowired
    private TleService tleService;

    @GetMapping("/{satNumber}")
    public TleData getTleData(@PathVariable String satNumber) {
        if (!isValidInteger(satNumber)) {
            throw new IllegalArgumentException("Invalid satellite number: " + satNumber);
        }
        return tleService.getTleData(satNumber);
    }

    @GetMapping("/most-fetched")
    public TleData getMostFetchedTleData() {
        return tleService.getMostFetchedTleData();
    }

    @GetMapping("/all")
    public List<TleData> getAllTleData() {
        return tleService.getAllTleData();
    }

    @GetMapping("/")
    public String defaultRoute() {
        return "Welcome to the Satellite TLE Data API";
    }

    private boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
