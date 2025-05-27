package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8081)
@TestPropertySource(properties = {
        "course-service.url=http://localhost:8081",
        "auth-service.url=http://localhost:8082"
})
class CourseClientTest {

    @Autowired
    private CourseClient courseClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void getCourseById_ReturnsCorrectCourseDetails() {
        Long courseId = 1L;

        stubFor(WireMock.get(urlEqualTo("/api/courses/public/" + courseId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "id": 1,
                              "title": "Advanced Programming",
                              "description": "Learn advanced programming concepts",
                              "tutorName": "John Doe",
                              "price": 100.00
                            }
                            """)));

        CourseDetailDTO result = courseClient.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Advanced Programming", result.getTitle());
        assertEquals("Learn advanced programming concepts", result.getDescription());

        verify(1, getRequestedFor(urlEqualTo("/api/courses/public/" + courseId)));
    }

    @Test
    void getCourseById_WithNonExistentId_HandlesNotFoundResponse() {
        Long nonExistentCourseId = 999L;

        stubFor(WireMock.get(urlEqualTo("/api/courses/public/" + nonExistentCourseId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            courseClient.getCourseById(nonExistentCourseId);
        });

        assertTrue(exception.getMessage().contains("404") ||
                exception.getMessage().contains("Not Found"));

        verify(1, getRequestedFor(urlEqualTo("/api/courses/public/" + nonExistentCourseId)));
    }

    @Test
    void getCourseById_WithServerError_HandlesErrorResponse() {
        Long courseId = 2L;

        stubFor(WireMock.get(urlEqualTo("/api/courses/public/" + courseId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            courseClient.getCourseById(courseId);
        });

        assertTrue(exception.getMessage().contains("500") ||
                exception.getMessage().contains("Server Error"));

        verify(1, getRequestedFor(urlEqualTo("/api/courses/public/" + courseId)));
    }
}