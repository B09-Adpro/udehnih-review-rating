package id.ac.ui.cs.advprog.udehnihreviewrating.config;

import id.ac.ui.cs.advprog.udehnihreviewrating.client.CourseClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.client.StudentClient;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableAutoConfiguration(exclude = {FeignAutoConfiguration.class})
public class MockFeignClientsConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public CourseClient courseClient() {
        return Mockito.mock(CourseClient.class);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public StudentClient studentClient() {
        return Mockito.mock(StudentClient.class);
    }
}