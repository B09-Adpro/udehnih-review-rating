package id.ac.ui.cs.advprog.udehnihreviewrating.service;

import id.ac.ui.cs.advprog.udehnihreviewrating.client.CourseClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.client.StudentClient;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.course.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.CreateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.request.UpdateReviewRequest;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.response.ReviewResponse;
import id.ac.ui.cs.advprog.udehnihreviewrating.dto.student.StudentDTO;
import id.ac.ui.cs.advprog.udehnihreviewrating.factory.ReviewFactory;
import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import id.ac.ui.cs.advprog.udehnihreviewrating.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import id.ac.ui.cs.advprog.udehnihreviewrating.exception.CourseNotFoundException;
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
    private final StudentClient studentClient;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewFactory reviewFactory, CourseClient courseClient, StudentClient studentClient) {
        this.reviewRepository = reviewRepository;
        this.reviewFactory = reviewFactory;
        this.courseClient = courseClient;
        this.studentClient = studentClient;
    }

    @Override
    public ReviewResponse createReview(Long studentId, CreateReviewRequest request) {
        validateCourseExists(request.getCourseId());
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
    public List<ReviewResponse> getReviewsByStudent(Long studentId) {
        List<Review> reviews = reviewRepository.findByStudentId(studentId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(UUID reviewId, Long studentId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getStudentId().equals(studentId) && review.getStudentId() != null) {
            throw new RuntimeException("Unauthorized to update this review");
        }

        validateCourseExists(review.getCourseId());

        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return convertToResponse(updatedReview);
    }

    private void validateCourseExists(Long courseId) {
        try {
            var course = courseClient.getCourseById(courseId);
            if (course == null) {
                throw new CourseNotFoundException(courseId);
            }
        } catch (Exception e) {
            if (e instanceof CourseNotFoundException) {
                throw e;
            }
            log.error("Error validating course {}: {}", courseId, e.getMessage());
            throw new RuntimeException("Course not found");
        }
    }

    @Override
    public boolean deleteReview(UUID reviewId, Long studentId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getStudentId().equals(studentId) && review.getStudentId() != null) {
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
        boolean isAnonymous = review.getStudentId() == null;

        CourseDetailDTO courseDetail;
        try {
            courseDetail = courseClient.getCourseById(review.getCourseId());
            if (courseDetail == null) {
                throw new RuntimeException("Course details not found for courseId: " + review.getCourseId());
            }

            if (courseDetail.getTitle() == null || courseDetail.getTitle().isEmpty()) {
                throw new RuntimeException("Course title is not defined for courseId: " + review.getCourseId());
            }

            if (courseDetail.getTutorName() == null || courseDetail.getTutorName().isEmpty()) {
                throw new RuntimeException("Tutor name is not defined for courseId: " + review.getCourseId());
            }
        } catch (Exception e) {
            log.error("Error fetching course details: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve course details: " + e.getMessage(), e);
        }

        String studentName = "Anonymous";
        if (!isAnonymous) {
            try {
                StudentDTO student = studentClient.getStudentById(review.getStudentId());
                if (student != null && student.getName() != null && !student.getName().isEmpty()) {
                    studentName = student.getName();
                }
            } catch (Exception e) {
                log.error("Error fetching student details: {}", e.getMessage());
            }
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourseId().toString())
                .courseName(courseDetail.getTitle())
                .studentId(isAnonymous ? null : review.getStudentId())
                .studentName(studentName)
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .isAnonymous(isAnonymous)
                .build();
    }
}