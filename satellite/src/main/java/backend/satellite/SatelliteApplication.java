package backend.satellite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SatelliteApplication {

    public static void main(String[] args) {
        // System.out.println("DB_URL from env: " + System.getenv("DB_URL"));
        // System.out.println("DB_USER from env: " + System.getenv("DB_USER"));
        // System.out.println("DB_PASSWORD from env: " + System.getenv("DB_PASSWORD"));
        SpringApplication.run(SatelliteApplication.class, args);
        System.out.println("Satellite Application Started");
    }
}