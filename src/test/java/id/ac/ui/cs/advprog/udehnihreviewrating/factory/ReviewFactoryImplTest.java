package id.ac.ui.cs.advprog.udehnihreviewrating.factory;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewFactoryImplTest {

    private ReviewFactory reviewFactory;
    private Long courseId;
    private Long studentId;

    @BeforeEach
    void setUp() {
        reviewFactory = new ReviewFactoryImpl();
        courseId = 123L;
        studentId = 456L;
    }

    @Test
    void createBasicReview_ValidInputs_ShouldCreateReview() {
        String reviewText = "Great course!";
        int rating = 5;

        Review review = reviewFactory.createBasicReview(courseId, studentId, reviewText, rating);

        assertNotNull(review);
        assertNotNull(review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
    }

    @Test
    void createBasicReview_InvalidRatingTooLow_ShouldThrowException() {
        String reviewText = "Great course!";
        int invalidRatingTooLow = 0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewFactory.createBasicReview(courseId, studentId, reviewText, invalidRatingTooLow);
        });
        assertEquals("Rating harus antara 1 and 5", exception.getMessage());
    }

    @Test
    void createBasicReview_InvalidRatingTooHigh_ShouldThrowException() {
        String reviewText = "Great course!";
        int invalidRatingTooHigh = 6;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewFactory.createBasicReview(courseId, studentId, reviewText, invalidRatingTooHigh);
        });
        assertEquals("Rating harus antara 1 and 5", exception.getMessage());
    }

    @Test
    void createRatingOnlyReview_ValidInputs_ShouldCreateReviewWithEmptyText() {
        int rating = 4;

        Review review = reviewFactory.createRatingOnlyReview(courseId, studentId, rating);

        assertNotNull(review);
        assertNotNull(review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals("", review.getReviewText());
        assertEquals(rating, review.getRating());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
    }

    @Test
    void createRatingOnlyReview_InvalidRating_ShouldThrowException() {
        int invalidRating = 0;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewFactory.createRatingOnlyReview(courseId, studentId, invalidRating);
        });
        assertEquals("Rating harus antara 1 and 5", exception.getMessage());
    }

    @Test
    void createDetailedReview_NonAnonymous_ShouldCreateReviewWithStudentId() {
        String reviewText = "Detailed review";
        int rating = 3;
        boolean isAnonymous = false;

        Review review = reviewFactory.createDetailedReview(courseId, studentId, reviewText, rating, isAnonymous);

        assertNotNull(review);
        assertNotNull(review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(studentId, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
    }

    @Test
    void createDetailedReview_Anonymous_ShouldCreateReviewWithAnonymousStudentId() {
        String reviewText = "Anonymous review";
        int rating = 2;
        boolean isAnonymous = true;

        Review review = reviewFactory.createDetailedReview(courseId, studentId, reviewText, rating, isAnonymous);

        assertNotNull(review);
        assertNotNull(review.getId());
        assertEquals(courseId, review.getCourseId());
        assertEquals(null, review.getStudentId());
        assertEquals(reviewText, review.getReviewText());
        assertEquals(rating, review.getRating());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
    }

    @Test
    void createDetailedReview_InvalidRating_ShouldThrowException() {
        String reviewText = "Detailed review";
        int invalidRating = 6;
        boolean isAnonymous = false;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewFactory.createDetailedReview(courseId, studentId, reviewText, invalidRating, isAnonymous);
        });
        assertEquals("Rating harus antara 1 and 5", exception.getMessage());
    }

    @Test
    void createDetailedReview_BoundaryRatingMin_ShouldCreateReview() {
        String reviewText = "Minimum rating";
        int minRating = 1;
        boolean isAnonymous = false;

        Review review = reviewFactory.createDetailedReview(courseId, studentId, reviewText, minRating, isAnonymous);

        assertNotNull(review);
        assertEquals(minRating, review.getRating());
    }

    @Test
    void createDetailedReview_BoundaryRatingMax_ShouldCreateReview() {
        String reviewText = "Maximum rating";
        int maxRating = 5;
        boolean isAnonymous = false;

        Review review = reviewFactory.createDetailedReview(courseId, studentId, reviewText, maxRating, isAnonymous);

        assertNotNull(review);
        assertEquals(maxRating, review.getRating());
    }
}