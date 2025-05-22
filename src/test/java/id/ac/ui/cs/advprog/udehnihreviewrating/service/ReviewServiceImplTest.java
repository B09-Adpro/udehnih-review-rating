package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.client.CourseClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.client.StudentClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.factory.ReviewFactory;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import id.ac.ui.cs.advprog.udehnihreviewrating.repository.ReviewRepository;
import id.ac.ui.cs.advprog.udehnihreviewrating.exception.CourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewFactory reviewFactory;

    @Mock
    private CourseClient courseClient;

    @Mock
    private StudentClient studentClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID reviewId;
    private Long courseId;
    private Long studentId;
    private Review review;
    private CourseDetailDTO courseDetail;
    private StudentDTO studentDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        courseId = 1L;
        studentId = 1L;
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

        courseDetail = CourseDetailDTO.builder()
                .id(courseId)
                .title("Advanced Programming")
                .description("Learn advanced programming concepts")
                .tutorName("John Doe")
                .price(new BigDecimal("100.00"))
                .build();

        studentDTO = StudentDTO.builder()
                .studentId(studentId)
                .email("student@example.com")
                .name("Jane Smith")
                .build();
    }

    @Test
    void createReview_WithReviewText_ShouldReturnReviewResponse() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(reviewFactory.createBasicReview(anyLong(), anyLong(), anyString(), anyInt()))
                .thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId.toString(), response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Jane Smith", response.getStudentName());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());

        verify(studentClient, times(2)).getStudentById(studentId);
        verify(courseClient, times(2)).getCourseById(courseId);
        verify(reviewFactory).createBasicReview(courseId, studentId, "Great course!", 5);
        verify(reviewRepository).save(review);
    }

    @Test
    void createReview_WithEmptyReviewText_ShouldCreateRatingOnlyReview() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("")
                .rating(4)
                .build();

        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(reviewFactory.createRatingOnlyReview(anyLong(), anyLong(), anyInt()))
                .thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        verify(reviewFactory).createRatingOnlyReview(courseId, studentId, 4);
    }

    @Test
    void createReview_StudentNotFound_ShouldThrowException() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(studentClient.getStudentById(studentId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(studentId, request);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_CourseNotFound_ShouldThrowException() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);
        when(courseClient.getCourseById(courseId)).thenReturn(null);

        assertThrows(CourseNotFoundException.class, () -> {
            reviewService.createReview(studentId, request);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getReviewById_ExistingReview_ShouldReturnReviewResponse() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);

        ReviewResponse response = reviewService.getReviewById(reviewId);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId.toString(), response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Jane Smith", response.getStudentName());

        verify(reviewRepository).findById(reviewId);
        verify(courseClient).getCourseById(courseId);
        verify(studentClient).getStudentById(studentId);
    }

    @Test
    void getReviewById_NonExistentReview_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });

        assertEquals("Review not found", exception.getMessage());
    }

    @Test
    void getReviewsByCourse_ExistingCourse_ShouldReturnListOfReviews() {
        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(2L)
                .reviewText("Good content")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<Review> reviews = Arrays.asList(review, review2);

        when(reviewRepository.findByCourseId(courseId)).thenReturn(reviews);
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(1L)).thenReturn(studentDTO);
        when(studentClient.getStudentById(2L)).thenReturn(
                StudentDTO.builder().studentId(2L).name("Bob Johnson").build());

        List<ReviewResponse> responses = reviewService.getReviewsByCourse(courseId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Advanced Programming", responses.get(0).getCourseName());
        assertEquals("Advanced Programming", responses.get(1).getCourseName());

        verify(reviewRepository).findByCourseId(courseId);
    }

    @Test
    void getReviewsByStudent_ExistingStudent_ShouldReturnListOfReviews() {
        List<Review> reviews = Arrays.asList(review);

        when(reviewRepository.findByStudentId(studentId)).thenReturn(reviews);
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);

        List<ReviewResponse> responses = reviewService.getReviewsByStudent(studentId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(studentId, responses.get(0).getStudentId());

        verify(reviewRepository).findByStudentId(studentId);
    }

    @Test
    void updateReview_ValidRequest_ShouldReturnUpdatedReview() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);

        ReviewResponse response = reviewService.updateReview(reviewId, studentId, request);

        assertNotNull(response);
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_UnauthorizedUser_ShouldThrowException() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(reviewId, 999L, request);
        });

        assertTrue(exception.getMessage().contains("Unauthorized to modify this review"));
    }

    @Test
    void deleteReview_ValidRequest_ShouldReturnTrue() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        boolean result = reviewService.deleteReview(reviewId, studentId);

        assertTrue(result);
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReview_UnauthorizedUser_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, 999L);
        });

        assertTrue(exception.getMessage().contains("Unauthorized to modify this review"));
    }

    @Test
    void getAverageRatingForCourse_WithReviews_ShouldReturnCorrectAverage() {
        Review review1 = Review.builder().rating(4).build();
        Review review2 = Review.builder().rating(5).build();
        Review review3 = Review.builder().rating(3).build();

        List<Review> reviews = Arrays.asList(review1, review2, review3);

        when(reviewRepository.findByCourseId(courseId)).thenReturn(reviews);

        double averageRating = reviewService.getAverageRatingForCourse(courseId);

        assertEquals(4.0, averageRating, 0.001);
        verify(reviewRepository).findByCourseId(courseId);
    }

    @Test
    void getAverageRatingForCourse_NoReviews_ShouldReturnZero() {
        when(reviewRepository.findByCourseId(courseId)).thenReturn(Arrays.asList());

        double averageRating = reviewService.getAverageRatingForCourse(courseId);

        assertEquals(0.0, averageRating);
    }

    @Test
    void convertToResponse_StudentNotFound_ShouldThrowException() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(studentId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });
    }

    @Test
    void convertToResponse_StudentWithEmptyName_ShouldThrowException() {
        StudentDTO studentWithEmptyName = StudentDTO.builder()
                .studentId(studentId)
                .email("student@example.com")
                .name("")
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(studentId)).thenReturn(studentWithEmptyName);

        assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });
    }

    @Test
    void convertToResponse_CourseWithMissingTitle_ShouldThrowException() {
        CourseDetailDTO courseWithoutTitle = CourseDetailDTO.builder()
                .id(courseId)
                .title("")
                .tutorName("John Doe")
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseWithoutTitle);

        assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });
    }

    @Test
    void convertToResponse_CourseWithMissingTutorName_ShouldThrowException() {
        CourseDetailDTO courseWithoutTutor = CourseDetailDTO.builder()
                .id(courseId)
                .title("Advanced Programming")
                .tutorName("")
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseWithoutTutor);

        assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(reviewId);
        });
    }

    @Test
    void validateStudentExists_ThrowsExceptionOnClientFailure() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(studentClient.getStudentById(studentId))
                .thenThrow(new RuntimeException("Student service error"));

        assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(studentId, request);
        });

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void validateCourseExists_ThrowsExceptionOnClientFailure() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);
        when(courseClient.getCourseById(courseId))
                .thenThrow(new RuntimeException("Course service error"));

        assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(studentId, request);
        });

        verify(reviewRepository, never()).save(any());
    }
}