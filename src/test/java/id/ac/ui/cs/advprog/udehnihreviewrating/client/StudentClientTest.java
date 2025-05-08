package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8090)
@TestPropertySource(properties = {
        "auth-service.url=http://localhost:8090",
        "spring.cloud.compatibility-verifier.enabled=false"
})
public class StudentClientTest {

    @Autowired
    private StudentClient studentClient;

    @Test
    void getStudentById_whenStudentExists_shouldReturnStudentDetails() {
        stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"1\",\"email\":\"student@example.com\",\"name\":\"John Doe\"}")));

        StudentDTO student = studentClient.getStudentById("1");

        assertNotNull(student);
        assertEquals("1", student.getId());
        assertEquals("student@example.com", student.getEmail());
        assertEquals("John Doe", student.getName());

        verify(getRequestedFor(urlEqualTo("/api/users/1")));
    }

    @Test
    void getStudentById_whenStudentDoesNotExist_shouldHandleErrorResponse() {
        stubFor(get(urlEqualTo("/api/users/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"User not found\"}")));

        assertThrows(Exception.class, () -> {
            studentClient.getStudentById("999");
        });

        verify(getRequestedFor(urlEqualTo("/api/users/999")));
    }
}