package id.ac.ui.cs.advprog.udehnihreviewrating.config;

import id.ac.ui.cs.advprog.udehnihreviewrating.controller.HealthController;
import id.ac.ui.cs.advprog.udehnihreviewrating.controller.ReviewController;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.udehnihreviewrating.service.ReviewService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {HealthController.class, ReviewController.class})
@Import({SecurityConfig.class, SecurityConfigTest.SliceBeans.class})
@TestPropertySource(properties = "COURSE_SERVICE_URL=localhost:8081")
class SecurityConfigTest {

    @Autowired private MockMvc mvc;
    @Autowired private ReviewService reviewService;

    private final UUID reviewId = UUID.randomUUID();

    @BeforeEach
    void stubService() {
        Mockito.reset(reviewService);
        Mockito.when(reviewService.getReviewById(any(UUID.class)))
                .thenReturn(ReviewResponse.builder().id(reviewId).build());
    }


    @Test
    void rootAndHealth_arePublic() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/health")).andExpect(status().isOk());
    }

    @Test
    void secureEndpoints_withoutAuth_return403() throws Exception {
        mvc.perform(get("/api/secure"))                      .andExpect(status().isForbidden());
        mvc.perform(get("/api/reviews/{id}", reviewId))      .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "STUDENT")
    void secureEndpoints_withAuth_return200() throws Exception {
        mvc.perform(get("/api/secure"))                      .andExpect(status().isOk());
        mvc.perform(get("/api/reviews/{id}", reviewId))      .andExpect(status().isOk());
    }


    @TestConfiguration
    static class SliceBeans {

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter() {
                @Override
                protected void doFilterInternal(
                        HttpServletRequest req,
                        HttpServletResponse res,
                        FilterChain chain)
                        throws ServletException, IOException {
                    chain.doFilter(req, res);
                }
            };
        }

        @Bean
        ReviewService reviewService() {
            return Mockito.mock(ReviewService.class);
        }
    }
}
