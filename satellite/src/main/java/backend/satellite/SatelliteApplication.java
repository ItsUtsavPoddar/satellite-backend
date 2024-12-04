package backend.satellite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SatelliteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SatelliteApplication.class, args);
        System.out.println("Satellite Application Started");
    }
}