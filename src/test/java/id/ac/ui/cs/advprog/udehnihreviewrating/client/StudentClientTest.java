package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8082)
@TestPropertySource(properties = {
        "course-service.url=http://localhost:8081",
        "auth-service.url=http://localhost:8082"
})
class StudentClientTest {

    @Autowired
    private StudentClient studentClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    void getStudentById_ReturnsCorrectStudentDetails() {
        Long studentId = 456L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "studentId": 456,
                              "email": "jane.smith@example.com",
                              "name": "Jane Smith"
                            }
                            """)));

        StudentDTO result = studentClient.getStudentById(studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("Jane Smith", result.getName());

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithCompleteStudentData_ShouldReturnAllFields() {
        Long studentId = 123L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "studentId": 123,
                              "email": "john.doe@university.edu",
                              "name": "John Doe"
                            }
                            """)));

        StudentDTO result = studentClient.getStudentById(studentId);

        assertNotNull(result);
        assertEquals(123L, result.getStudentId());
        assertEquals("john.doe@university.edu", result.getEmail());
        assertEquals("John Doe", result.getName());

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithNonExistentId_HandlesNotFoundResponse() {
        Long nonExistentStudentId = 999L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + nonExistentStudentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(nonExistentStudentId);
        });

        assertTrue(exception.getMessage().contains("404") ||
                exception.getMessage().contains("Not Found"));

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + nonExistentStudentId)));
    }

    @Test
    void getStudentById_WithUnauthorizedAccess_HandlesUnauthorizedResponse() {
        Long studentId = 789L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.UNAUTHORIZED.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(studentId);
        });

        assertTrue(exception.getMessage().contains("401") ||
                exception.getMessage().contains("Unauthorized"));

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithForbiddenAccess_HandlesForbiddenResponse() {
        Long studentId = 555L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.FORBIDDEN.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(studentId);
        });

        assertTrue(exception.getMessage().contains("403") ||
                exception.getMessage().contains("Forbidden"));

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithServerError_HandlesErrorResponse() {
        Long studentId = 222L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(studentId);
        });

        assertTrue(exception.getMessage().contains("500") ||
                exception.getMessage().contains("Server Error"));

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithServiceUnavailable_HandlesServiceUnavailableResponse() {
        Long studentId = 333L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(studentId);
        });

        assertTrue(exception.getMessage().contains("503") ||
                exception.getMessage().contains("Service Unavailable"));

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithInvalidJsonResponse_HandlesParsingError() {
        Long studentId = 777L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{ invalid json content }")));

        Exception exception = assertThrows(Exception.class, () -> {
            studentClient.getStudentById(studentId);
        });

        assertNotNull(exception);
        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithMissingFields_HandlesIncompleteData() {
        Long studentId = 888L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "studentId": 888,
                              "email": "incomplete@example.com"
                            }
                            """)));

        StudentDTO result = studentClient.getStudentById(studentId);

        assertNotNull(result);
        assertEquals(888L, result.getStudentId());
        assertEquals("incomplete@example.com", result.getEmail());
        assertNull(result.getName());

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithEmptyName_ShouldReturnEmptyString() {
        Long studentId = 101L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "studentId": 101,
                              "email": "empty.name@example.com",
                              "name": ""
                            }
                            """)));

        StudentDTO result = studentClient.getStudentById(studentId);

        assertNotNull(result);
        assertEquals(101L, result.getStudentId());
        assertEquals("empty.name@example.com", result.getEmail());
        assertEquals("", result.getName());

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }

    @Test
    void getStudentById_WithSpecialCharactersInName_ShouldHandleCorrectly() {
        Long studentId = 111L;

        stubFor(WireMock.get(urlEqualTo("/api/users/" + studentId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                            {
                              "studentId": 111,
                              "email": "special@example.com",
                              "name": "José María García-Rodríguez"
                            }
                            """)));

        StudentDTO result = studentClient.getStudentById(studentId);

        assertNotNull(result);
        assertEquals(111L, result.getStudentId());
        assertEquals("special@example.com", result.getEmail());
        assertEquals("José María García-Rodríguez", result.getName());

        verify(1, getRequestedFor(urlEqualTo("/api/users/" + studentId)));
    }
}
