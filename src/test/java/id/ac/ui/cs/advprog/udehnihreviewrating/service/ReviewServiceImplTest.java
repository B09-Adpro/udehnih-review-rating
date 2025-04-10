package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.factory.ReviewFactory;
import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import id.ac.ui.cs.advprog.udehnihreviewrating.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewFactory reviewFactory;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID reviewId;
    private String courseId;
    private String studentId;
    private Review review;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        courseId = "COURSE-123";
        studentId = "STUDENT-456";
        now = LocalDateTime.now();

        review = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId(studentId)
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createReview_BasicReview_ShouldReturnReviewResponse() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(false)
                .build();

        when(reviewFactory.createBasicReview(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());

        verify(reviewFactory).createBasicReview(courseId, studentId, "Great course!", 5);
        verify(reviewRepository).save(review);
    }

    @Test
    void createReview_RatingOnlyReview_ShouldReturnReviewResponse() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("")
                .rating(4)
                .anonymous(false)
                .build();

        Review ratingOnlyReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId(studentId)
                .reviewText("")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewFactory.createRatingOnlyReview(anyString(), anyString(), anyInt()))
                .thenReturn(ratingOnlyReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(ratingOnlyReview);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertEquals(studentId, response.getStudentId());
        assertEquals("", response.getReviewText());
        assertEquals(4, response.getRating());

        verify(reviewFactory).createRatingOnlyReview(courseId, studentId, 4);
        verify(reviewRepository).save(ratingOnlyReview);
    }

    @Test
    void createReview_AnonymousReview_ShouldReturnAnonymousReviewResponse() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Anonymous review")
                .rating(3)
                .anonymous(true)
                .build();

        Review anonymousReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId("anonymous")
                .reviewText("Anonymous review")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewFactory.createDetailedReview(anyString(), anyString(), anyString(), anyInt(), eq(true)))
                .thenReturn(anonymousReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(anonymousReview);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertNull(response.getStudentId());
        assertEquals("Anonymous review", response.getReviewText());
        assertEquals(3, response.getRating());
        assertEquals("Anonymous", response.getStudentName());
        assertTrue(response.isAnonymous());

        verify(reviewFactory).createDetailedReview(courseId, studentId, "Anonymous review", 3, true);
        verify(reviewRepository).save(anonymousReview);
    }

    @Test
    void getReviewById_ExistingReview_ShouldReturnReviewResponse() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewResponse response = reviewService.getReviewById(reviewId);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());

        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void getReviewById_NonExistingReview_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void getReviewsByCourse_ExistingCourseWithReviews_ShouldReturnListOfReviewResponses() {
        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId("STUDENT-789")
                .reviewText("Good content")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Review> reviews = Arrays.asList(review, review2);

        when(reviewRepository.findByCourseId(courseId)).thenReturn(reviews);

        List<ReviewResponse> responses = reviewService.getReviewsByCourse(courseId);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        ReviewResponse response1 = responses.get(0);
        assertEquals(review.getId(), response1.getId());
        assertEquals(review.getCourseId(), response1.getCourseId());
        assertEquals(review.getStudentId(), response1.getStudentId());
        assertEquals(review.getReviewText(), response1.getReviewText());
        assertEquals(review.getRating(), response1.getRating());

        ReviewResponse response2 = responses.get(1);
        assertEquals(review2.getId(), response2.getId());
        assertEquals(review2.getCourseId(), response2.getCourseId());
        assertEquals(review2.getStudentId(), response2.getStudentId());
        assertEquals(review2.getReviewText(), response2.getReviewText());
        assertEquals(review2.getRating(), response2.getRating());

        verify(reviewRepository).findByCourseId(courseId);
    }

    @Test
    void getReviewsByStudent_ExistingStudentWithReviews_ShouldReturnListOfReviewResponses() {
        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId("COURSE-456")
                .studentId(studentId)
                .reviewText("Excellent material")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Review> reviews = Arrays.asList(review, review2);

        when(reviewRepository.findByStudentId(studentId)).thenReturn(reviews);

        List<ReviewResponse> responses = reviewService.getReviewsByStudent(studentId);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        ReviewResponse response1 = responses.get(0);
        assertEquals(review.getId(), response1.getId());
        assertEquals(review.getCourseId(), response1.getCourseId());
        assertEquals(review.getStudentId(), response1.getStudentId());
        assertEquals(review.getReviewText(), response1.getReviewText());
        assertEquals(review.getRating(), response1.getRating());

        ReviewResponse response2 = responses.get(1);
        assertEquals(review2.getId(), response2.getId());
        assertEquals(review2.getCourseId(), response2.getCourseId());
        assertEquals(review2.getStudentId(), response2.getStudentId());
        assertEquals(review2.getReviewText(), response2.getReviewText());
        assertEquals(review2.getRating(), response2.getRating());

        verify(reviewRepository).findByStudentId(studentId);
    }

    @Test
    void updateReview_ExistingReviewAndAuthorizedStudent_ShouldReturnUpdatedReviewResponse() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Review updatedReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId(studentId)
                .reviewText("Updated review text")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        ReviewResponse response = reviewService.updateReview(reviewId, studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Updated review text", response.getReviewText());
        assertEquals(4, response.getRating());

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_ExistingReviewButUnauthorizedStudent_ShouldThrowException() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(reviewId, "DIFFERENT-STUDENT", request);
        });

        assertEquals("Unauthorized to update this review", exception.getMessage());
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_NonExistingReview_ShouldThrowException() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(reviewId, studentId, request);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ExistingReviewAndAuthorizedStudent_ShouldReturnTrue() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).deleteById(reviewId);

        boolean result = reviewService.deleteReview(reviewId, studentId);

        assertTrue(result);
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReview_ExistingReviewButUnauthorizedStudent_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, "DIFFERENT-STUDENT");
        });

        assertEquals("Unauthorized to delete this review", exception.getMessage());
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void deleteReview_NonExistingReview_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, studentId);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void getAverageRatingForCourse_CourseWithReviews_ShouldReturnCorrectAverage() {
        Review review1 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId("STUDENT-1")
                .rating(4)
                .build();

        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId("STUDENT-2")
                .rating(5)
                .build();

        Review review3 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId("STUDENT-3")
                .rating(3)
                .build();

        List<Review> reviews = Arrays.asList(review1, review2, review3);

        when(reviewRepository.findByCourseId(courseId)).thenReturn(reviews);

        double averageRating = reviewService.getAverageRatingForCourse(courseId);

        assertEquals(4.0, averageRating, 0.001);
        verify(reviewRepository).findByCourseId(courseId);
    }

    @Test
    void getAverageRatingForCourse_CourseWithNoReviews_ShouldReturnZero() {
        when(reviewRepository.findByCourseId(courseId)).thenReturn(List.of());

        double averageRating = reviewService.getAverageRatingForCourse(courseId);

        assertEquals(0.0, averageRating, 0.001);
        verify(reviewRepository).findByCourseId(courseId);
    }

    @Test
    void updateReview_AnonymousReview_ShouldAllowUpdate() {
        Review anonymousReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId("anonymous")
                .reviewText("Anonymous review")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated anonymous review")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(anonymousReview));

        Review updatedReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId("anonymous")
                .reviewText("Updated anonymous review")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        ReviewResponse response = reviewService.updateReview(reviewId, "DIFFERENT-STUDENT", request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId, response.getCourseId());
        assertNull(response.getStudentId());
        assertEquals("Updated anonymous review", response.getReviewText());
        assertEquals(4, response.getRating());
        assertTrue(response.isAnonymous());

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void deleteReview_AnonymousReview_ShouldAllowDeletion() {
        Review anonymousReview = Review.builder()
                .id(reviewId)
                .courseId(courseId)
                .studentId("anonymous")
                .reviewText("Anonymous review")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(anonymousReview));
        doNothing().when(reviewRepository).deleteById(reviewId);

        boolean result = reviewService.deleteReview(reviewId, "DIFFERENT-STUDENT");

        assertTrue(result);
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }
}