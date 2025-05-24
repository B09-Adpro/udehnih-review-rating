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
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewFactory reviewFactory,
                             CourseClient courseClient, StudentClient studentClient) {
        this.reviewRepository = reviewRepository;
        this.reviewFactory = reviewFactory;
        this.courseClient = courseClient;
        this.studentClient = studentClient;
    }

    @Override
    public ReviewResponse createReview(Long studentId, CreateReviewRequest request) {
        validateStudentExists(studentId);
        validateCourseExists(request.getCourseId());

        Review review = createReviewBasedOnType(studentId, request);
        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(UUID reviewId) {
        Review review = findReviewById(reviewId);
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
        Review review = findReviewById(reviewId);
        validateReviewOwnership(review, studentId);
        validateCourseExists(review.getCourseId());

        updateReviewFields(review, request);
        Review updatedReview = reviewRepository.save(review);

        return convertToResponse(updatedReview);
    }

    @Override
    public boolean deleteReview(UUID reviewId, Long studentId) {
        Review review = findReviewById(reviewId);
        validateReviewOwnership(review, studentId);

        reviewRepository.deleteById(reviewId);
        return true;
    }

    @Override
    public double getAverageRatingForCourse(Long courseId) {
        List<Review> courseReviews = reviewRepository.findByCourseId(courseId);

        if (courseReviews.isEmpty()) {
            return 0.0;
        }

        return courseReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private Review createReviewBasedOnType(Long studentId, CreateReviewRequest request) {
        if (isEmptyReviewText(request.getReviewText())) {
            return reviewFactory.createRatingOnlyReview(
                    request.getCourseId(),
                    studentId,
                    request.getRating()
            );
        } else {
            return reviewFactory.createBasicReview(
                    request.getCourseId(),
                    studentId,
                    request.getReviewText(),
                    request.getRating()
            );
        }
    }

    private boolean isEmptyReviewText(String reviewText) {
        return reviewText == null || reviewText.trim().isEmpty();
    }

    private Review findReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    private void validateReviewOwnership(Review review, Long studentId) {
        if (!review.getStudentId().equals(studentId)) {
            throw new RuntimeException("Unauthorized to modify this review");
        }
    }

    private void updateReviewFields(Review review, UpdateReviewRequest request) {
        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        review.setUpdatedAt(LocalDateTime.now());
    }

    private void validateStudentExists(Long studentId) {
        try {
            StudentDTO student = studentClient.getStudentById(studentId);
            if (student == null) {
                throw new RuntimeException("Student not found or unauthorized");
            }
        } catch (Exception e) {
            log.error("Error validating student {}: {}", studentId, e.getMessage());
            throw new RuntimeException("Student validation failed - unauthorized or not found");
        }
    }

    private void validateCourseExists(Long courseId) {
        try {
            CourseDetailDTO course = courseClient.getCourseById(courseId);
            if (course == null) {
                throw new CourseNotFoundException(courseId);
            }
        } catch (CourseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating course {}: {}", courseId, e.getMessage());
            throw new RuntimeException("Course not found");
        }
    }

    private CourseDetailDTO getCourseDetails(Long courseId) {
        try {
            CourseDetailDTO courseDetail = courseClient.getCourseById(courseId);
            validateCourseDetail(courseDetail, courseId);
            return courseDetail;
        } catch (Exception e) {
            log.error("Error fetching course details: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve course details: " + e.getMessage(), e);
        }
    }

    private void validateCourseDetail(CourseDetailDTO courseDetail, Long courseId) {
        if (courseDetail == null) {
            throw new RuntimeException("Course details not found for courseId: " + courseId);
        }

        if (isEmptyString(courseDetail.getTitle())) {
            throw new RuntimeException("Course title is not defined for courseId: " + courseId);
        }
    }

    private String getStudentDisplayName(Long studentId) {
        try {
            StudentDTO student = studentClient.getStudentById(studentId);
            if (student != null && !isEmptyString(student.getName())) {
                return student.getName();
            }

            throw new RuntimeException("Student data incomplete - name is missing for studentId: " + studentId);
        } catch (Exception e) {
            log.error("Error fetching student details for studentId {}: {}", studentId, e.getMessage());
            throw new RuntimeException("Failed to retrieve student information for studentId: " + studentId, e);
        }
    }

    private boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }

    private ReviewResponse convertToResponse(Review review) {
        CourseDetailDTO courseDetail = getCourseDetails(review.getCourseId());
        String studentName = getStudentDisplayName(review.getStudentId());

        String courseName = courseDetail.getTitle();
        if (isEmptyString(courseName)) {
            courseName = "Course " + courseDetail.getId();
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourseId().toString())
                .courseName(courseName)
                .studentId(review.getStudentId())
                .studentName(studentName)
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}