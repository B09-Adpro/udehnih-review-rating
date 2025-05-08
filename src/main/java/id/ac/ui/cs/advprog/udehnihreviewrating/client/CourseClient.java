package id.ac.ui.cs.advprog.udehnihreviewrating.client;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", url = "${course-service.url}")
public interface CourseClient {

    @GetMapping("/api/courses/{courseId}")
    CourseDetailDTO getCourseById(@PathVariable("courseId") Long courseId);
}