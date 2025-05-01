package id.ac.ui.cs.advprog.udehnihreviewrating.controller;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.security.StudentDetails;
import id.ac.ui.cs.advprog.udehnihreviewrating.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody CreateReviewRequest request) {
        String studentId = studentDetails.getId();
        ReviewResponse response = reviewService.createReview(studentId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable UUID reviewId) {
        ReviewResponse response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCourse(@PathVariable Long courseId) {
        List<ReviewResponse> responses = reviewService.getReviewsByCourse(courseId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByStudent(@PathVariable String studentId) {
        List<ReviewResponse> responses = reviewService.getReviewsByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody UpdateReviewRequest request) {
        String studentId = studentDetails.getId();
        ReviewResponse response = reviewService.updateReview(reviewId, studentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Boolean> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal StudentDetails studentDetails) {
        String studentId = studentDetails.getId();
        boolean deleted = reviewService.deleteReview(reviewId, studentId);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/course/{courseId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForCourse(@PathVariable Long courseId) {
        double averageRating = reviewService.getAverageRatingForCourse(courseId);
        return ResponseEntity.ok(averageRating);
    }
}