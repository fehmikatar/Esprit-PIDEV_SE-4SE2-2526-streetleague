package tn.esprit._4se2.pi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< Updated upstream

@SpringBootApplication
=======
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
>>>>>>> Stashed changes
public class PiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PiApplication.class, args);
    }
}