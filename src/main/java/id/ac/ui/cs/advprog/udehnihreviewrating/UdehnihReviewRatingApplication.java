package id.ac.ui.cs.advprog.udehnihreviewrating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UdehnihReviewRatingApplication {
    public static void main(String[] args) {
        SpringApplication.run(UdehnihReviewRatingApplication.class, args);
    }
}