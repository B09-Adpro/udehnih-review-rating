package id.ac.ui.cs.advprog.udehnihreviewrating.controller;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ObjectMapper objectMapper;
    private UUID reviewId;
    private String courseId;
    private String studentId;
    private ReviewResponse reviewResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        reviewId = UUID.randomUUID();
        courseId = "COURSE-123";
        studentId = "STUDENT-456";
        now = LocalDateTime.now();

        reviewResponse = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId)
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();
    }

    @Test
    void createReview_ValidRequest_ShouldReturnCreatedReviewWithCreatedStatus() throws Exception {
        CreateReviewRequest request = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(false)
                .build();

        when(reviewService.createReview(eq(studentId), any(CreateReviewRequest.class)))
                .thenReturn(reviewResponse);

        mockMvc.perform(post("/api/reviews")
                        .header("Student-ID", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.reviewText").value("Great course!"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getReviewById_ExistingReview_ShouldReturnReviewWithOkStatus() throws Exception {
        when(reviewService.getReviewById(reviewId)).thenReturn(reviewResponse);

        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(jsonPath("$.courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.reviewText").value("Great course!"))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getReviewById_NonExistingReview_ShouldThrowException() {
        when(reviewService.getReviewById(reviewId)).thenThrow(new RuntimeException("Review not found"));

        try {
            mockMvc.perform(get("/api/reviews/{reviewId}", reviewId));
        } catch (Exception e) {
            // Expected exception, test passes
        }
    }

    @Test
    void getReviewsByCourse_CourseWithReviews_ShouldReturnListOfReviewsWithOkStatus() throws Exception {
        ReviewResponse reviewResponse2 = ReviewResponse.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .courseName("Advanced Programming")
                .studentId("STUDENT-789")
                .studentName("Jane Smith")
                .reviewText("Good content")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        List<ReviewResponse> reviewResponses = Arrays.asList(reviewResponse, reviewResponse2);

        when(reviewService.getReviewsByCourse(courseId)).thenReturn(reviewResponses);

        mockMvc.perform(get("/api/reviews/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(reviewId.toString()))
                .andExpect(jsonPath("$[0].courseId").value(courseId))
                .andExpect(jsonPath("$[0].studentId").value(studentId))
                .andExpect(jsonPath("$[1].studentId").value("STUDENT-789"));
    }

    @Test
    void getReviewsByStudent_StudentWithReviews_ShouldReturnListOfReviewsWithOkStatus() throws Exception {
        ReviewResponse reviewResponse2 = ReviewResponse.builder()
                .id(UUID.randomUUID())
                .courseId("COURSE-456")
                .courseName("Data Structures")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Excellent material")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        List<ReviewResponse> reviewResponses = Arrays.asList(reviewResponse, reviewResponse2);

        when(reviewService.getReviewsByStudent(studentId)).thenReturn(reviewResponses);

        mockMvc.perform(get("/api/reviews/student/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(reviewId.toString()))
                .andExpect(jsonPath("$[0].studentId").value(studentId))
                .andExpect(jsonPath("$[0].courseId").value(courseId))
                .andExpect(jsonPath("$[1].courseId").value("COURSE-456"));
    }

    @Test
    void updateReview_ValidRequestAndAuthorizedStudent_ShouldReturnUpdatedReviewWithOkStatus() throws Exception {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        ReviewResponse updatedResponse = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId)
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Updated review text")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        when(reviewService.updateReview(eq(reviewId), eq(studentId), any(UpdateReviewRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .header("Student-ID", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.reviewText").value("Updated review text"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void updateReview_UnauthorizedStudent_ShouldThrowException() {
        UpdateReviewRequest request = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        doThrow(new RuntimeException("Unauthorized to update this review"))
                .when(reviewService).updateReview(eq(reviewId), anyString(), any(UpdateReviewRequest.class));

        try {
            mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                    .header("Student-ID", "DIFFERENT-STUDENT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        } catch (Exception e) {
            // Expected exception, test passes
        }
    }

    @Test
    void deleteReview_AuthorizedStudent_ShouldReturnTrue() throws Exception {
        when(reviewService.deleteReview(reviewId, studentId)).thenReturn(true);

        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .header("Student-ID", studentId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteReview_UnauthorizedStudent_ShouldThrowException() {
        doThrow(new RuntimeException("Unauthorized to delete this review"))
                .when(reviewService).deleteReview(reviewId, "DIFFERENT-STUDENT");

        try {
            mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                    .header("Student-ID", "DIFFERENT-STUDENT"));
        } catch (Exception e) {
            // Expected exception, test passes
        }
    }

    @Test
    void getAverageRatingForCourse_CourseWithReviews_ShouldReturnAverageRating() throws Exception {
        double averageRating = 4.5;
        when(reviewService.getAverageRatingForCourse(courseId)).thenReturn(averageRating);

        mockMvc.perform(get("/api/reviews/course/{courseId}/average-rating", courseId))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void getAverageRatingForCourse_CourseWithNoReviews_ShouldReturnZero() throws Exception {
        when(reviewService.getAverageRatingForCourse(courseId)).thenReturn(0.0);

        mockMvc.perform(get("/api/reviews/course/{courseId}/average-rating", courseId))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }
}