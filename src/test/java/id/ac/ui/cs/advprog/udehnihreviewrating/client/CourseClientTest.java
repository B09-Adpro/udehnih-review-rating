package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8089)
@TestPropertySource(properties = {
        "course-service.url=http://localhost:8089",
        "spring.cloud.compatibility-verifier.enabled=false"
})
public class CourseClientTest {

    @Autowired
    private CourseClient courseClient;

    @Test
    void getCourseById_whenCourseExists_shouldReturnCourseDetails() {
        stubFor(get(urlEqualTo("/api/courses/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"title\":\"Advanced Programming\",\"description\":\"Learn advanced programming concepts\",\"tutorName\":\"John Doe\",\"price\":100.00}")));

        CourseDetailDTO courseDetail = courseClient.getCourseById(1L);

        assertNotNull(courseDetail);
        assertEquals(1L, courseDetail.getId());
        assertEquals("Advanced Programming", courseDetail.getTitle());
        assertEquals("Learn advanced programming concepts", courseDetail.getDescription());
        assertEquals("John Doe", courseDetail.getTutorName());
        assertEquals(new BigDecimal("100.00"), courseDetail.getPrice());

        verify(getRequestedFor(urlEqualTo("/api/courses/1")));
    }

    @Test
    void getCourseById_whenCourseDoesNotExist_shouldHandleNotFoundResponse() {
        stubFor(get(urlEqualTo("/api/courses/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Course not found\"}")));

        Exception exception = assertThrows(Exception.class, () -> {
            courseClient.getCourseById(999L);
        });

        verify(getRequestedFor(urlEqualTo("/api/courses/999")));
    }
}