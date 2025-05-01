package id.ac.ui.cs.advprog.udehnihreviewrating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.compatibility-verifier.enabled=false"
})
class UdehnihReviewRatingApplicationTests {

    @Test
    void contextLoads() {

    }
}