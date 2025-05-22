package id.ac.ui.cs.advprog.udehnihreviewrating.dto;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void createReviewRequest_BuilderAndGettersSetters() {
        Long courseId = 123L;
        String reviewText = "Great course!";
        int rating = 5;

        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText(reviewText)
                .rating(rating)
                .build();

        assertEquals(courseId, request.getCourseId());
        assertEquals(reviewText, request.getReviewText());
        assertEquals(rating, request.getRating());

        CreateReviewRequest request2 = new CreateReviewRequest();
        request2.setCourseId(456L);
        request2.setReviewText("Different review");
        request2.setRating(4);

        assertEquals(456L, request2.getCourseId());
        assertEquals("Different review", request2.getReviewText());
        assertEquals(4, request2.getRating());
    }

    @Test
    void updateReviewRequest_BuilderAndGettersSetters() {
        String reviewText = "Updated review text";
        int rating = 4;

        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText(reviewText)
                .rating(rating)
                .build();

        assertEquals(reviewText, request.getReviewText());
        assertEquals(rating, request.getRating());

        UpdateReviewRequest request2 = new UpdateReviewRequest();
        request2.setReviewText("Different update");
        request2.setRating(3);

        assertEquals("Different update", request2.getReviewText());
        assertEquals(3, request2.getRating());
    }

    @Test
    void reviewResponse_BuilderAndGettersSetters() {
        UUID id = UUID.randomUUID();
        String courseId = "123";
        String courseName = "Advanced Programming";
        Long studentId = 456L;
        String studentName = "John Doe";
        String reviewText = "Great course!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        ReviewResponse response = ReviewResponse.builder()
                .id(id)
                .courseId(courseId)
                .courseName(courseName)
                .studentId(studentId)
                .studentName(studentName)
                .reviewText(reviewText)
                .rating(rating)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertEquals(courseName, response.getCourseName());
        assertEquals(studentId, response.getStudentId());
        assertEquals(studentName, response.getStudentName());
        assertEquals(reviewText, response.getReviewText());
        assertEquals(rating, response.getRating());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());

        ReviewResponse response2 = new ReviewResponse();
        UUID id2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        response2.setId(id2);
        response2.setCourseId("456");
        response2.setCourseName("Data Structures");
        response2.setStudentId(789L);
        response2.setStudentName("Jane Smith");
        response2.setReviewText("Different review");
        response2.setRating(4);
        response2.setCreatedAt(now);
        response2.setUpdatedAt(now);

        assertEquals(id2, response2.getId());
        assertEquals("456", response2.getCourseId());
        assertEquals("Data Structures", response2.getCourseName());
        assertEquals(789L, response2.getStudentId());
        assertEquals("Jane Smith", response2.getStudentName());
        assertEquals("Different review", response2.getReviewText());
        assertEquals(4, response2.getRating());
        assertEquals(now, response2.getCreatedAt());
        assertEquals(now, response2.getUpdatedAt());
    }

    @Test
    void createReviewRequest_NoArgsConstructor() {
        CreateReviewRequest request = new CreateReviewRequest();

        assertNull(request.getCourseId());
        assertNull(request.getReviewText());
        assertEquals(0, request.getRating());
    }

    @Test
    void updateReviewRequest_NoArgsConstructor() {
        UpdateReviewRequest request = new UpdateReviewRequest();

        assertNull(request.getReviewText());
        assertEquals(0, request.getRating());
    }

    @Test
    void reviewResponse_NoArgsConstructor() {
        ReviewResponse response = new ReviewResponse();

        assertNull(response.getId());
        assertNull(response.getCourseId());
        assertNull(response.getCourseName());
        assertNull(response.getStudentId());
        assertNull(response.getStudentName());
        assertNull(response.getReviewText());
        assertEquals(0, response.getRating());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void createReviewRequest_AllArgsConstructor() {
        CreateReviewRequest request = new CreateReviewRequest(
                123L,
                "Great course!",
                5
        );

        assertEquals(123L, request.getCourseId());
        assertEquals("Great course!", request.getReviewText());
        assertEquals(5, request.getRating());
    }

    @Test
    void updateReviewRequest_AllArgsConstructor() {
        UpdateReviewRequest request = new UpdateReviewRequest(
                "Updated text",
                4
        );

        assertEquals("Updated text", request.getReviewText());
        assertEquals(4, request.getRating());
    }

    @Test
    void reviewResponse_AllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewResponse response = new ReviewResponse(
                id,
                "123",
                "Advanced Programming",
                456L,
                "John Doe",
                "Great course!",
                5,
                now,
                now
        );

        assertEquals(id, response.getId());
        assertEquals("123", response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals(456L, response.getStudentId());
        assertEquals("John Doe", response.getStudentName());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void createReviewRequest_EqualsAndHashCode() {
        CreateReviewRequest request1 = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .build();

        CreateReviewRequest request2 = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .build();

        CreateReviewRequest request3 = CreateReviewRequest.builder()
                .courseId(999L)
                .reviewText("Different text")
                .rating(3)
                .build();

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void updateReviewRequest_EqualsAndHashCode() {
        UpdateReviewRequest request1 = UpdateReviewRequest.builder()
                .reviewText("Updated text")
                .rating(4)
                .build();

        UpdateReviewRequest request2 = UpdateReviewRequest.builder()
                .reviewText("Updated text")
                .rating(4)
                .build();

        UpdateReviewRequest request3 = UpdateReviewRequest.builder()
                .reviewText("Different text")
                .rating(3)
                .build();

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void reviewResponse_EqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewResponse response1 = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId(456L)
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ReviewResponse response2 = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId(456L)
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ReviewResponse response3 = ReviewResponse.builder()
                .id(UUID.randomUUID())
                .courseId("999")
                .courseName("Different")
                .studentId(999L)
                .studentName("Different")
                .reviewText("Different")
                .rating(3)
                .createdAt(now.plusDays(1))
                .updatedAt(now.plusDays(1))
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void dtos_ToString() {
        CreateReviewRequest createRequest = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .build();

        UpdateReviewRequest updateRequest = UpdateReviewRequest.builder()
                .reviewText("Updated text")
                .rating(4)
                .build();

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ReviewResponse response = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId(456L)
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        String createToString = createRequest.toString();
        String updateToString = updateRequest.toString();
        String responseToString = response.toString();

        assertTrue(createToString.contains("123"));
        assertTrue(createToString.contains("Great course!"));
        assertTrue(createToString.contains("5"));

        assertTrue(updateToString.contains("Updated text"));
        assertTrue(updateToString.contains("4"));

        assertTrue(responseToString.contains(id.toString()));
        assertTrue(responseToString.contains("123"));
        assertTrue(responseToString.contains("Advanced Programming"));
        assertTrue(responseToString.contains("456"));
        assertTrue(responseToString.contains("John Doe"));
    }
}