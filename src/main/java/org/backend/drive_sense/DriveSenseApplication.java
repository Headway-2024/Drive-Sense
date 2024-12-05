package org.backend.drive_sense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.backend.drive_sense")
public class DriveSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriveSenseApplication.class, args);
    }

}
