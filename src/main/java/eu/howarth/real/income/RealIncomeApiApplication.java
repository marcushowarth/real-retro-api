package eu.howarth.real.income;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealIncomeApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealIncomeApiApplication.class, args);
    }
}
