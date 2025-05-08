package id.ac.ui.cs.advprog.udehnihreviewrating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "COURSE_SERVICE_URL=http://localhost:8089",
        "AUTH_SERVICE_URL=http://localhost:8090",
        "spring.cloud.compatibility-verifier.enabled=false"
})
class UdehnihReviewRatingApplicationTests {

    @Test
    void contextLoads() {

    }
}