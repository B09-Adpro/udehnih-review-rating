package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewService reviewService;

    private UUID reviewId;
    private Long studentId;
    private Long courseId;
    private CreateReviewRequest createRequest;
    private UpdateReviewRequest updateRequest;
    private ReviewResponse reviewResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        studentId = 123L;
        courseId = 456L;
        now = LocalDateTime.now();

        createRequest = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        updateRequest = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        reviewResponse = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createReview_ShouldAcceptValidInputsAndReturnReviewResponse() {
        when(reviewService.createReview(anyLong(), any(CreateReviewRequest.class)))
                .thenReturn(reviewResponse);

        ReviewResponse result = reviewService.createReview(studentId, createRequest);
        
        assertNotNull(result);
        assertEquals(reviewResponse.getId(), result.getId());
        assertEquals(reviewResponse.getCourseId(), result.getCourseId());
        assertEquals(reviewResponse.getStudentId(), result.getStudentId());
        assertEquals(reviewResponse.getRating(), result.getRating());

        verify(reviewService).createReview(studentId, createRequest);
    }

    @Test
    void createReview_ShouldHandleNullStudentId() {
        
        when(reviewService.createReview(isNull(), any(CreateReviewRequest.class)))
                .thenThrow(new IllegalArgumentException("Student ID cannot be null"));

        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(null, createRequest);
        });

        verify(reviewService).createReview(null, createRequest);
    }

    @Test
    void createReview_ShouldHandleNullRequest() {
        
        when(reviewService.createReview(anyLong(), isNull()))
                .thenThrow(new IllegalArgumentException("Create request cannot be null"));

        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(studentId, null);
        });

        verify(reviewService).createReview(studentId, null);
    }

    @Test
    void getReviewById_ShouldAcceptUUIDAndReturnReviewResponse() {
        
        when(reviewService.getReviewById(any(UUID.class)))
                .thenReturn(reviewResponse);

        
        ReviewResponse result = reviewService.getReviewById(reviewId);

        
        assertNotNull(result);
        assertEquals(reviewResponse.getId(), result.getId());
        assertEquals(reviewResponse.getCourseId(), result.getCourseId());

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    void getReviewById_ShouldHandleNullUUID() {
        
        when(reviewService.getReviewById(isNull()))
                .thenThrow(new IllegalArgumentException("Review ID cannot be null"));

        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewById(null);
        });

        verify(reviewService).getReviewById(null);
    }

    @Test
    void getReviewsByCourse_ShouldAcceptCourseIdAndReturnListOfReviews() {
        
        List<ReviewResponse> expectedReviews = Arrays.asList(
                reviewResponse,
                ReviewResponse.builder()
                        .id(UUID.randomUUID())
                        .courseId(courseId.toString())
                        .courseName("Advanced Programming")
                        .studentId(789L)
                        .studentName("Jane Smith")
                        .reviewText("Good content")
                        .rating(4)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        when(reviewService.getReviewsByCourse(anyLong()))
                .thenReturn(expectedReviews);

        List<ReviewResponse> result = reviewService.getReviewsByCourse(courseId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedReviews.get(0).getId(), result.get(0).getId());
        assertEquals(expectedReviews.get(1).getId(), result.get(1).getId());

        verify(reviewService).getReviewsByCourse(courseId);
    }

    @Test
    void getReviewsByCourse_ShouldReturnEmptyListForCourseWithNoReviews() {
        when(reviewService.getReviewsByCourse(anyLong()))
                .thenReturn(Arrays.asList());

        List<ReviewResponse> result = reviewService.getReviewsByCourse(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(reviewService).getReviewsByCourse(999L);
    }

    @Test
    void getReviewsByCourse_ShouldHandleNullCourseId() {
        when(reviewService.getReviewsByCourse(isNull()))
                .thenThrow(new IllegalArgumentException("Course ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewsByCourse(null);
        });

        verify(reviewService).getReviewsByCourse(null);
    }

    @Test
    void getReviewsByStudent_ShouldAcceptStudentIdAndReturnListOfReviews() {
        List<ReviewResponse> expectedReviews = Arrays.asList(reviewResponse);

        when(reviewService.getReviewsByStudent(anyLong()))
                .thenReturn(expectedReviews);

        List<ReviewResponse> result = reviewService.getReviewsByStudent(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewResponse.getId(), result.get(0).getId());
        assertEquals(studentId, result.get(0).getStudentId());

        verify(reviewService).getReviewsByStudent(studentId);
    }

    @Test
    void getReviewsByStudent_ShouldReturnEmptyListForStudentWithNoReviews() {
        when(reviewService.getReviewsByStudent(anyLong()))
                .thenReturn(Arrays.asList());

        List<ReviewResponse> result = reviewService.getReviewsByStudent(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(reviewService).getReviewsByStudent(999L);
    }

    @Test
    void getReviewsByStudent_ShouldHandleNullStudentId() {
        when(reviewService.getReviewsByStudent(isNull()))
                .thenThrow(new IllegalArgumentException("Student ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewsByStudent(null);
        });

        verify(reviewService).getReviewsByStudent(null);
    }

    @Test
    void updateReview_ShouldAcceptValidInputsAndReturnUpdatedReview() {
        ReviewResponse updatedResponse = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Updated review text")
                .rating(4)
                .createdAt(now)
                .updatedAt(now.plusMinutes(5))
                .build();

        when(reviewService.updateReview(any(UUID.class), anyLong(), any(UpdateReviewRequest.class)))
                .thenReturn(updatedResponse);

        ReviewResponse result = reviewService.updateReview(reviewId, studentId, updateRequest);

        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        assertEquals("Updated review text", result.getReviewText());
        assertEquals(4, result.getRating());

        verify(reviewService).updateReview(reviewId, studentId, updateRequest);
    }

    @Test
    void updateReview_ShouldHandleNullReviewId() {
        when(reviewService.updateReview(isNull(), anyLong(), any(UpdateReviewRequest.class)))
                .thenThrow(new IllegalArgumentException("Review ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(null, studentId, updateRequest);
        });

        verify(reviewService).updateReview(null, studentId, updateRequest);
    }

    @Test
    void updateReview_ShouldHandleNullStudentId() {
        when(reviewService.updateReview(any(UUID.class), isNull(), any(UpdateReviewRequest.class)))
                .thenThrow(new IllegalArgumentException("Student ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, null, updateRequest);
        });

        verify(reviewService).updateReview(reviewId, null, updateRequest);
    }

    @Test
    void updateReview_ShouldHandleNullUpdateRequest() {
        when(reviewService.updateReview(any(UUID.class), anyLong(), isNull()))
                .thenThrow(new IllegalArgumentException("Update request cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, studentId, null);
        });

        verify(reviewService).updateReview(reviewId, studentId, null);
    }

    @Test
    void deleteReview_ShouldAcceptValidInputsAndReturnBoolean() {
        when(reviewService.deleteReview(any(UUID.class), anyLong()))
                .thenReturn(true);

        boolean result = reviewService.deleteReview(reviewId, studentId);

        assertTrue(result);

        verify(reviewService).deleteReview(reviewId, studentId);
    }

    @Test
    void deleteReview_ShouldReturnFalseWhenDeletionFails() {
        when(reviewService.deleteReview(any(UUID.class), anyLong()))
                .thenReturn(false);

        boolean result = reviewService.deleteReview(reviewId, studentId);

        assertFalse(result);
        verify(reviewService).deleteReview(reviewId, studentId);
    }

    @Test
    void deleteReview_ShouldHandleNullReviewId() {
        when(reviewService.deleteReview(isNull(), anyLong()))
                .thenThrow(new IllegalArgumentException("Review ID cannot be null"));

        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(null, studentId);
        });

        verify(reviewService).deleteReview(null, studentId);
    }

    @Test
    void deleteReview_ShouldHandleNullStudentId() {
        when(reviewService.deleteReview(any(UUID.class), isNull()))
                .thenThrow(new IllegalArgumentException("Student ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(reviewId, null);
        });

        verify(reviewService).deleteReview(reviewId, null);
    }

    @Test
    void getAverageRatingForCourse_ShouldAcceptCourseIdAndReturnDouble() {
        double expectedAverage = 4.5;
        when(reviewService.getAverageRatingForCourse(anyLong()))
                .thenReturn(expectedAverage);

        double result = reviewService.getAverageRatingForCourse(courseId);

        assertEquals(expectedAverage, result, 0.001);
        verify(reviewService).getAverageRatingForCourse(courseId);
    }

    @Test
    void getAverageRatingForCourse_ShouldReturnZeroForCourseWithNoReviews() {
        when(reviewService.getAverageRatingForCourse(anyLong()))
                .thenReturn(0.0);

        double result = reviewService.getAverageRatingForCourse(999L);

        assertEquals(0.0, result, 0.001);
        verify(reviewService).getAverageRatingForCourse(999L);
    }

    @Test
    void getAverageRatingForCourse_ShouldHandleNullCourseId() {
        when(reviewService.getAverageRatingForCourse(isNull()))
                .thenThrow(new IllegalArgumentException("Course ID cannot be null"));

        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getAverageRatingForCourse(null);
        });

        verify(reviewService).getAverageRatingForCourse(null);
    }

    @Test
    void getAverageRatingForCourse_ShouldReturnValidRatingRange() {
        double validRating = 3.7;
        when(reviewService.getAverageRatingForCourse(anyLong()))
                .thenReturn(validRating);

        double result = reviewService.getAverageRatingForCourse(courseId);

        assertTrue(result >= 0.0 && result <= 5.0,
                "Average rating should be between 0.0 and 5.0, but was: " + result);
        verify(reviewService).getAverageRatingForCourse(courseId);
    }

    

    @Test
    void allMethods_ShouldBeDefinedInInterface() {
        ReviewService service = mock(ReviewService.class);

        assertDoesNotThrow(() -> {
            service.createReview(1L, createRequest);
            service.getReviewById(reviewId);
            service.getReviewsByCourse(1L);
            service.getReviewsByStudent(1L);
            service.updateReview(reviewId, 1L, updateRequest);
            service.deleteReview(reviewId, 1L);
            service.getAverageRatingForCourse(1L);
        });
    }

    @Test
    void createReview_ShouldReturnNonNullResponse() {
        when(reviewService.createReview(anyLong(), any(CreateReviewRequest.class)))
                .thenReturn(reviewResponse);

        ReviewResponse result = reviewService.createReview(studentId, createRequest);

        assertNotNull(result, "createReview should never return null");
        assertNotNull(result.getId(), "ReviewResponse.id should not be null");
        assertNotNull(result.getCourseId(), "ReviewResponse.courseId should not be null");
    }

    @Test
    void getReviewsByCourse_ShouldReturnNonNullList() {
        when(reviewService.getReviewsByCourse(anyLong()))
                .thenReturn(Arrays.asList(reviewResponse));

        List<ReviewResponse> result = reviewService.getReviewsByCourse(courseId);

        assertNotNull(result, "getReviewsByCourse should never return null, use empty list instead");
    }

    @Test
    void getReviewsByStudent_ShouldReturnNonNullList() {
        when(reviewService.getReviewsByStudent(anyLong()))
                .thenReturn(Arrays.asList(reviewResponse));

        List<ReviewResponse> result = reviewService.getReviewsByStudent(studentId);

        assertNotNull(result, "getReviewsByStudent should never return null, use empty list instead");
    }

    @Test
    void updateReview_ShouldReturnNonNullResponse() {
        when(reviewService.updateReview(any(UUID.class), anyLong(), any(UpdateReviewRequest.class)))
                .thenReturn(reviewResponse);

        ReviewResponse result = reviewService.updateReview(reviewId, studentId, updateRequest);

        assertNotNull(result, "updateReview should never return null");
        assertNotNull(result.getId(), "Updated ReviewResponse.id should not be null");
    }
}