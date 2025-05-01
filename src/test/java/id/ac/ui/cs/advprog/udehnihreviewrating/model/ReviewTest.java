package id.ac.ui.cs.advprog.udehnihreviewrating.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testReviewBuilder() {
        UUID id = UUID.randomUUID();
        Long courseId = 123L;
        String studentId = "STUDENT-456";
        String reviewText = "This is a great course!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Review review = Review.builder()
                .id(id)
                .courseId(courseId)
                .studentId(studentId)
                .reviewText(reviewText)
                .rating(rating)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(id, review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testReviewNoArgsConstructor() {
        Review review = new Review();

        assertNull(review.getId());
        assertNull(review.getCourseId());
        assertNull(review.getStudentId());
        assertNull(review.getReviewText());
        assertEquals(0, review.getRating());
        assertNull(review.getCreatedAt());
        assertNull(review.getUpdatedAt());
    }

    @Test
    void testReviewAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Long courseId = 123L;
        String studentId = "STUDENT-456";
        String reviewText = "This is a great course!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Review review = new Review(id, courseId, studentId, reviewText, rating, createdAt, updatedAt);

        assertEquals(id, review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testReviewSettersAndGetters() {
        Review review = new Review();
        UUID id = UUID.randomUUID();
        Long courseId = 123L;
        String studentId = "STUDENT-456";
        String reviewText = "This is a great course!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        review.setId(id);
        review.setCourseId(courseId);
        review.setStudentId(studentId);
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(updatedAt);

        assertEquals(id, review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }

    @Test
    void testReviewEqualsAndHashCode() {
        UUID id = UUID.randomUUID();

        Review review1 = Review.builder()
                .id(id)
                .courseId(123L)
                .studentId("STUDENT-456")
                .reviewText("This is a great course!")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .id(id)
                .courseId(789L)
                .studentId("STUDENT-999")
                .reviewText("Different text")
                .rating(3)
                .createdAt(LocalDateTime.now().plusDays(1))
                .updatedAt(LocalDateTime.now().plusDays(1))
                .build();

        Review review3 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(123L)
                .studentId("STUDENT-456")
                .reviewText("This is a great course!")
                .rating(5)
                .createdAt(review1.getCreatedAt())
                .updatedAt(review1.getUpdatedAt())
                .build();

        assertEquals(review1, review1);
        assertEquals(review1, review2);
        assertNotEquals(review1, review3);
        assertNotEquals(review1, null);
        assertNotEquals(review1, new Object());

        assertEquals(review1.hashCode(), review2.hashCode());
        assertNotEquals(review1.hashCode(), review3.hashCode());
    }

    @Test
    void testReviewToString() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(id)
                .courseId(123L)
                .studentId("STUDENT-456")
                .reviewText("This is a great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        String toString = review.toString();

        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("STUDENT-456"));
        assertTrue(toString.contains("This is a great course!"));
        assertTrue(toString.contains("5"));
    }
}