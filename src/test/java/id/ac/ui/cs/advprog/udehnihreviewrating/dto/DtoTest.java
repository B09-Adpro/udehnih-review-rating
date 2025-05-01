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
        boolean anonymous = true;

        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText(reviewText)
                .rating(rating)
                .anonymous(anonymous)
                .build();

        assertEquals(courseId, request.getCourseId());
        assertEquals(reviewText, request.getReviewText());
        assertEquals(rating, request.getRating());
        assertTrue(request.isAnonymous());

        CreateReviewRequest request2 = new CreateReviewRequest();
        request2.setCourseId(456L);
        request2.setReviewText("Different review");
        request2.setRating(4);
        request2.setAnonymous(false);

        assertEquals(456L, request2.getCourseId());
        assertEquals("Different review", request2.getReviewText());
        assertEquals(4, request2.getRating());
        assertFalse(request2.isAnonymous());
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
        String studentId = "STUDENT-456";
        String studentName = "John Doe";
        String reviewText = "Great course!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        boolean isAnonymous = false;

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
                .isAnonymous(isAnonymous)
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
        assertFalse(response.isAnonymous());

        ReviewResponse response2 = new ReviewResponse();
        UUID id2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        response2.setId(id2);
        response2.setCourseId("456");
        response2.setCourseName("Data Structures");
        response2.setStudentId("STUDENT-789");
        response2.setStudentName("Jane Smith");
        response2.setReviewText("Different review");
        response2.setRating(4);
        response2.setCreatedAt(now);
        response2.setUpdatedAt(now);
        response2.setAnonymous(true);

        assertEquals(id2, response2.getId());
        assertEquals("456", response2.getCourseId());
        assertEquals("Data Structures", response2.getCourseName());
        assertEquals("STUDENT-789", response2.getStudentId());
        assertEquals("Jane Smith", response2.getStudentName());
        assertEquals("Different review", response2.getReviewText());
        assertEquals(4, response2.getRating());
        assertEquals(now, response2.getCreatedAt());
        assertEquals(now, response2.getUpdatedAt());
        assertTrue(response2.isAnonymous());
    }

    @Test
    void createReviewRequest_NoArgsConstructor() {
        CreateReviewRequest request = new CreateReviewRequest();

        assertNull(request.getCourseId());
        assertNull(request.getReviewText());
        assertEquals(0, request.getRating());
        assertFalse(request.isAnonymous());
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
        assertFalse(response.isAnonymous());
    }

    @Test
    void createReviewRequest_AllArgsConstructor() {
        CreateReviewRequest request = new CreateReviewRequest(
                123L,
                "Great course!",
                5,
                true
        );

        assertEquals(123L, request.getCourseId());
        assertEquals("Great course!", request.getReviewText());
        assertEquals(5, request.getRating());
        assertTrue(request.isAnonymous());
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
                "STUDENT-456",
                "John Doe",
                "Great course!",
                5,
                now,
                now,
                false
        );

        assertEquals(id, response.getId());
        assertEquals("123", response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals("STUDENT-456", response.getStudentId());
        assertEquals("John Doe", response.getStudentName());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
        assertFalse(response.isAnonymous());
    }

    @Test
    void createReviewRequest_ToString() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(true)
                .build();

        String toString = request.toString();

        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("Great course!"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("anonymous=true"));
    }

    @Test
    void updateReviewRequest_ToString() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated text")
                .rating(4)
                .build();

        String toString = request.toString();

        assertTrue(toString.contains("Updated text"));
        assertTrue(toString.contains("4"));
    }

    @Test
    void reviewResponse_ToString() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewResponse response = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId("STUDENT-456")
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        String toString = response.toString();

        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("Advanced Programming"));
        assertTrue(toString.contains("STUDENT-456"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("Great course!"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains(now.toString()));
        assertTrue(toString.contains("isAnonymous=false"));
    }

    @Test
    void createReviewRequest_EqualsAndHashCode() {
        CreateReviewRequest request1 = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(true)
                .build();

        CreateReviewRequest request2 = CreateReviewRequest.builder()
                .courseId(123L)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(true)
                .build();

        CreateReviewRequest request3 = CreateReviewRequest.builder()
                .courseId(999L)
                .reviewText("Different text")
                .rating(3)
                .anonymous(false)
                .build();

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
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
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void reviewResponse_EqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ReviewResponse response1 = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId("STUDENT-456")
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        ReviewResponse response2 = ReviewResponse.builder()
                .id(id)
                .courseId("123")
                .courseName("Advanced Programming")
                .studentId("STUDENT-456")
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        ReviewResponse response3 = ReviewResponse.builder()
                .id(UUID.randomUUID())
                .courseId("999")
                .courseName("Different")
                .studentId("DIFFERENT")
                .studentName("Different")
                .reviewText("Different")
                .rating(3)
                .createdAt(now.plusDays(1))
                .updatedAt(now.plusDays(1))
                .isAnonymous(true)
                .build();

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}