package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface StudentClient {
    @GetMapping("/api/users/{studentId}")
    StudentDTO getStudentById(@PathVariable("studentId") String studentId);
}