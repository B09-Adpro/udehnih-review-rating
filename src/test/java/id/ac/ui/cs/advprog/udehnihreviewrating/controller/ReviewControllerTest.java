package id.ac.ui.cs.advprog.udehnihreviewrating.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.security.StudentDetails;
import id.ac.ui.cs.advprog.udehnihreviewrating.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = {FeignAutoConfiguration.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.openfeign.enabled=false",
        "COURSE_SERVICE_URL=http://localhost:8081",
        "AUTH_SERVICE_URL=http://localhost:8082"
})
class ReviewControllerTest {

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    StudentDetails principal;
    UUID reviewId;
    Long courseId;
    Long studentId;
    LocalDateTime now;

    @BeforeEach
    void init() {
        reviewId = UUID.randomUUID();
        courseId = 1L;
        studentId = 456L;
        now = LocalDateTime.now();
        principal = new StudentDetails(
                studentId,
                "student@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
    }

    @Test
    void createReview_WithReviewText_ShouldReturnCreated() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        ReviewResponse res = ReviewResponse.builder()
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

        when(reviewService.createReview(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(CreateReviewRequest.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.courseId").value(courseId.toString()))
                .andExpect(jsonPath("$.courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$.studentId").value(studentId))
                .andExpect(jsonPath("$.studentName").value("John Doe"))
                .andExpect(jsonPath("$.reviewText").value("Great course!"))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService).createReview(
                ArgumentMatchers.eq(studentId),
                ArgumentMatchers.any(CreateReviewRequest.class));
    }

    @Test
    void createReview_RatingOnly_ShouldReturnCreated() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("")
                .rating(4)
                .build();

        ReviewResponse res = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reviewService.createReview(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(CreateReviewRequest.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.reviewText").value(""))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.studentId").value(studentId));

        verify(reviewService).createReview(
                ArgumentMatchers.eq(studentId),
                ArgumentMatchers.any(CreateReviewRequest.class));
    }

    @Test
    void getReviewById_ShouldReturnOk() throws Exception {
        ReviewResponse res = ReviewResponse.builder()
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

        when(reviewService.getReviewById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(res);

        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$.studentName").value("John Doe"))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService).getReviewById(ArgumentMatchers.eq(reviewId));
    }

    @Test
    void getReviewsByCourse_ShouldReturnOk() throws Exception {
        ReviewResponse review1 = ReviewResponse.builder()
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

        ReviewResponse review2 = ReviewResponse.builder()
                .id(UUID.randomUUID())
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId(789L)
                .studentName("Jane Smith")
                .reviewText("Good content")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        List<ReviewResponse> reviews = Arrays.asList(review1, review2);

        when(reviewService.getReviewsByCourse(ArgumentMatchers.anyLong()))
                .thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/course/{courseId}", courseId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$[0].studentName").value("John Doe"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[1].studentName").value("Jane Smith"))
                .andExpect(jsonPath("$[1].rating").value(4));

        verify(reviewService).getReviewsByCourse(ArgumentMatchers.eq(courseId));
    }

    @Test
    void getReviewsByStudent_ShouldReturnOk() throws Exception {
        ReviewResponse review = ReviewResponse.builder()
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

        List<ReviewResponse> reviews = Arrays.asList(review);

        when(reviewService.getReviewsByStudent(ArgumentMatchers.anyLong()))
                .thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/student/{studentId}", studentId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value(studentId))
                .andExpect(jsonPath("$[0].studentName").value("John Doe"));

        verify(reviewService).getReviewsByStudent(ArgumentMatchers.eq(studentId));
    }

    @Test
    void updateReview_ShouldReturnOk() throws Exception {
        UpdateReviewRequest req = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        ReviewResponse upd = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId(studentId)
                .studentName("John Doe")
                .reviewText("Updated review text")
                .rating(4)
                .createdAt(now)
                .updatedAt(now.plusMinutes(10))
                .build();

        when(reviewService.updateReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(UpdateReviewRequest.class)))
                .thenReturn(upd);

        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.reviewText").value("Updated review text"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.studentId").value(studentId));

        verify(reviewService).updateReview(
                ArgumentMatchers.eq(reviewId),
                ArgumentMatchers.eq(studentId),
                ArgumentMatchers.any(UpdateReviewRequest.class));
    }

    @Test
    void deleteReview_ShouldReturnTrue() throws Exception {
        when(reviewService.deleteReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyLong()))
                .thenReturn(true);

        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(reviewService).deleteReview(
                ArgumentMatchers.eq(reviewId),
                ArgumentMatchers.eq(studentId));
    }

    @Test
    void getAverageRatingForCourse_ShouldReturnOk() throws Exception {
        double averageRating = 4.5;

        when(reviewService.getAverageRatingForCourse(ArgumentMatchers.anyLong()))
                .thenReturn(averageRating);

        mockMvc.perform(get("/api/reviews/course/{courseId}/average-rating", courseId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));

        verify(reviewService).getAverageRatingForCourse(ArgumentMatchers.eq(courseId));
    }

    @Test
    void createReview_InvalidJson_ShouldReturnInternalServerError() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createReview_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        mockMvc.perform(post("/api/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateReview_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        UpdateReviewRequest req = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteReview_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createReview_ServiceException_ShouldReturnInternalServerError() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .build();

        when(reviewService.createReview(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(CreateReviewRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReviewById_NonExistentReview_ShouldReturnInternalServerError() throws Exception {
        when(reviewService.getReviewById(ArgumentMatchers.any(UUID.class)))
                .thenThrow(new RuntimeException("Review not found"));

        mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal)))
                .andExpect(status().isInternalServerError());

        verify(reviewService).getReviewById(ArgumentMatchers.eq(reviewId));
    }

    @Test
    void updateReview_UnauthorizedUser_ShouldReturnInternalServerError() throws Exception {
        UpdateReviewRequest req = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        when(reviewService.updateReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(UpdateReviewRequest.class)))
                .thenThrow(new RuntimeException("Unauthorized to modify this review"));

        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteReview_UnauthorizedUser_ShouldReturnInternalServerError() throws Exception {
        when(reviewService.deleteReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyLong()))
                .thenThrow(new RuntimeException("Unauthorized to modify this review"));

        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createReview_MissingRequestBody_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getReviewById_InvalidUUID_ShouldReturnInternalServerError() throws Exception {
        String invalidUUID = "invalid-uuid";

        mockMvc.perform(get("/api/reviews/{reviewId}", invalidUUID)
                        .with(user(principal)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateReview_InvalidUUID_ShouldReturnInternalServerError() throws Exception {
        UpdateReviewRequest req = UpdateReviewRequest.builder()
                .reviewText("Updated review text")
                .rating(4)
                .build();

        String invalidUUID = "invalid-uuid";

        mockMvc.perform(put("/api/reviews/{reviewId}", invalidUUID)
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteReview_InvalidUUID_ShouldReturnInternalServerError() throws Exception {
        String invalidUUID = "invalid-uuid";

        mockMvc.perform(delete("/api/reviews/{reviewId}", invalidUUID)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}