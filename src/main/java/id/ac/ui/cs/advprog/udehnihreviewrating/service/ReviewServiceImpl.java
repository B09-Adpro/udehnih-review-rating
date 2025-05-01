package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.client.CourseClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.factory.ReviewFactory;
import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import id.ac.ui.cs.advprog.udehnihreviewrating.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewFactory reviewFactory;
    private final CourseClient courseClient;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewFactory reviewFactory, CourseClient courseClient) {
        this.reviewRepository = reviewRepository;
        this.reviewFactory = reviewFactory;
        this.courseClient = courseClient;
    }

    @Override
    public ReviewResponse createReview(String studentId, CreateReviewRequest request) {
        Review review;

        if (request.isAnonymous()) {
            review = reviewFactory.createDetailedReview(
                    request.getCourseId(),
                    studentId,
                    request.getReviewText(),
                    request.getRating(),
                    true
            );
        } else if (request.getReviewText() == null || request.getReviewText().isEmpty()) {
            review = reviewFactory.createRatingOnlyReview(
                    request.getCourseId(),
                    studentId,
                    request.getRating()
            );
        } else {
            review = reviewFactory.createBasicReview(
                    request.getCourseId(),
                    studentId,
                    request.getReviewText(),
                    request.getRating()
            );
        }

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return convertToResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByCourse(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseId(courseId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByStudent(String studentId) {
        List<Review> reviews = reviewRepository.findByStudentId(studentId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(UUID reviewId, String studentId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getStudentId().equals(studentId) && !review.getStudentId().equals("anonymous")) {
            throw new RuntimeException("Unauthorized to update this review");
        }

        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);

        return convertToResponse(updatedReview);
    }

    @Override
    public boolean deleteReview(UUID reviewId, String studentId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getStudentId().equals(studentId) && !review.getStudentId().equals("anonymous")) {
            throw new RuntimeException("Unauthorized to delete this review");
        }

        reviewRepository.deleteById(reviewId);

        return true;
    }

    @Override
    public double getAverageRatingForCourse(Long courseId) {
        List<Review> courseReviews = reviewRepository.findByCourseId(courseId);

        if (courseReviews.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (Review review : courseReviews) {
            sum += review.getRating();
        }

        return (double) sum / courseReviews.size();
    }

    private ReviewResponse convertToResponse(Review review) {
        boolean isAnonymous = "anonymous".equals(review.getStudentId());

        String courseName = "Course Name";
        String tutorName = "Tutor Name";

        try {
            CourseDetailDTO courseDetail = courseClient.getCourseById(review.getCourseId());
            if (courseDetail != null) {
                courseName = courseDetail.getTitle();
                tutorName = courseDetail.getTutorName();
            }
        } catch (Exception e) {
            log.error("Error fetching course details: {}", e.getMessage());
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourseId().toString())
                .courseName(courseName)
                .studentId(isAnonymous ? null : review.getStudentId())
                .studentName(isAnonymous ? "Anonymous" : "Student Name")
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .isAnonymous(isAnonymous)
                .build();
    }
}