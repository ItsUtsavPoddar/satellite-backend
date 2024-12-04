package backend.satellite.controller;

import backend.satellite.model.TleData;
import backend.satellite.service.TleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class TleController {

    @Autowired
    private TleService tleService;

    @GetMapping("/{satNumber}")
    public TleData getTleData(@PathVariable String satNumber) {
        return tleService.getTleData(satNumber);
    }
   @GetMapping("/")
    public String defaultRoute() {
        return "Welcome to the Satellite TLE Data API";
    }
}

