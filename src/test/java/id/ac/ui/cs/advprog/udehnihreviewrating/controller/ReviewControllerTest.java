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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
@Import(ReviewControllerTest.MockConfig.class)
@TestPropertySource(properties = "COURSE_SERVICE_URL=localhost:8081")
class ReviewControllerTest {

    static class MockConfig {
        @Bean
        ReviewService reviewService() {
            return Mockito.mock(ReviewService.class);
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ObjectMapper objectMapper;

    StudentDetails principal;
    UUID reviewId;
    Long courseId;
    LocalDateTime now;

    @BeforeEach
    void init() {
        reviewId = UUID.randomUUID();
        courseId = 1L;
        now = LocalDateTime.now();
        principal = new StudentDetails(
                "STUDENT-456",
                "student@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
    }

    @Test
    void createReview_ShouldReturnCreated() throws Exception {
        CreateReviewRequest req = CreateReviewRequest.builder()
                .courseId(courseId)
                .reviewText("Great course!")
                .rating(5)
                .anonymous(false)
                .build();

        ReviewResponse res = ReviewResponse.builder()
                .id(reviewId)
                .courseId(courseId.toString())
                .courseName("Advanced Programming")
                .studentId("STUDENT-456")
                .studentName("John Doe")
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        when(reviewService.createReview(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(CreateReviewRequest.class)))
                .thenReturn(res);

        mockMvc.perform(post("/api/reviews")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService).createReview(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(CreateReviewRequest.class));
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
                .studentId("STUDENT-456")
                .studentName("John Doe")
                .reviewText("Updated review text")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .isAnonymous(false)
                .build();

        when(reviewService.updateReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(UpdateReviewRequest.class)))
                .thenReturn(upd);

        mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.rating").value(4));

        verify(reviewService).updateReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(UpdateReviewRequest.class));
    }

    @Test
    void deleteReview_ShouldReturnTrue() throws Exception {
        when(reviewService.deleteReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyString()))
                .thenReturn(true);

        mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
                        .with(user(principal))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(reviewService).deleteReview(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyString());
    }
}
