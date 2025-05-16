package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import id.ac.ui.cs.advprog.udehnihreviewrating.config.FeignConfig;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth-service.url}", configuration = FeignConfig.class)
public interface StudentClient {
    @GetMapping("/api/users/{studentId}")
    StudentDTO getStudentById(@PathVariable("studentId") Long studentId);
}