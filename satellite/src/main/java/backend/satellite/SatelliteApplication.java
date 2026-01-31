package backend.satellite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SatelliteApplication {

    private static final Logger logger = LoggerFactory.getLogger(SatelliteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SatelliteApplication.class, args);
        logger.info("Satellite Backend Application Started Successfully");
    }
}