package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(String studentId, CreateReviewRequest request);
    ReviewResponse getReviewById(UUID reviewId);
    List<ReviewResponse> getReviewsByCourse(Long courseId);
    List<ReviewResponse> getReviewsByStudent(String studentId);
    ReviewResponse updateReview(UUID reviewId, String studentId, UpdateReviewRequest request);
    boolean deleteReview(UUID reviewId, String studentId);
    double getAverageRatingForCourse(Long courseId);
}