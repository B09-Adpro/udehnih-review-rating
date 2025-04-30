package id.ac.ui.cs.advprog.udehnihreviewrating.config;

import id.ac.ui.cs.advprog.udehnihreviewrating.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {SecurityConfigTest.TestConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicEndpoints() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews/course/123/average-rating"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews/course/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/reviews/123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testProtectedEndpointsWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reviews/123"))
                .andExpect(status().isOk());
    }

    @Configuration
    @Import(SecurityConfig.class)
    static class TestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return mock(JwtAuthenticationFilter.class);
        }
    }
}