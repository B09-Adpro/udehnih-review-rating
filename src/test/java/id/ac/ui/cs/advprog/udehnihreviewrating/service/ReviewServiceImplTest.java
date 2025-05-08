package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.client.CourseClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.client.StudentClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.factory.ReviewFactory;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import id.ac.ui.cs.advprog.udehnihreviewrating.repository.ReviewRepository;
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
    private String studentId;
    private Review review;
    private CourseDetailDTO courseDetail;
    private StudentDTO studentDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        courseId = 1L;
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

        courseDetail = CourseDetailDTO.builder()
                .id(courseId)
                .title("Advanced Programming")
                .description("Learn advanced programming concepts")
                .tutorName("John Doe")
                .price(new BigDecimal("100.00"))
                .build();

        studentDTO = StudentDTO.builder()
                .id(studentId)
                .email("student@example.com")
                .name("Jane Smith")
                .build();
    }

    @Test
    void createReview_BasicReview_ShouldReturnReviewResponseWithStudentAndCourseDetails() {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(false)
                .build();

        when(reviewFactory.createBasicReview(anyLong(), anyString(), anyString(), anyInt()))
                .thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);
        when(studentClient.getStudentById(studentId)).thenReturn(studentDTO);

        ReviewResponse response = reviewService.createReview(studentId, request);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId.toString(), response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Jane Smith", response.getStudentName());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());

        verify(reviewFactory).createBasicReview(courseId, studentId, "Great course!", 5);
        verify(reviewRepository).save(review);
        verify(courseClient).getCourseById(courseId);
        verify(studentClient).getStudentById(studentId);
    }

    @Test
    void getReviewById_ExistingReviewWithCourseDetails_ShouldReturnReviewResponse() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);

        ReviewResponse response = reviewService.getReviewById(reviewId);

        assertNotNull(response);
        assertEquals(reviewId, response.getId());
        assertEquals(courseId.toString(), response.getCourseId());
        assertEquals("Advanced Programming", response.getCourseName());
        assertEquals(studentId, response.getStudentId());
        assertEquals("Great course!", response.getReviewText());
        assertEquals(5, response.getRating());

        verify(reviewRepository).findById(reviewId);
        verify(courseClient).getCourseById(courseId);
    }

    @Test
    void getReviewsByCourse_ExistingCourse_ShouldReturnListOfReviewResponses() {
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
        when(courseClient.getCourseById(courseId)).thenReturn(courseDetail);

        List<ReviewResponse> responses = reviewService.getReviewsByCourse(courseId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Advanced Programming", responses.get(0).getCourseName());
        assertEquals("Advanced Programming", responses.get(1).getCourseName());

        verify(reviewRepository).findByCourseId(courseId);
        verify(courseClient, times(2)).getCourseById(courseId);
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
}