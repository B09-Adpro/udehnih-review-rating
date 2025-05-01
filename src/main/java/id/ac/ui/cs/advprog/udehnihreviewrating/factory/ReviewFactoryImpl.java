package id.ac.ui.cs.advprog.udehnihreviewrating.factory;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ReviewFactoryImpl implements ReviewFactory {

    @Override
    public Review createBasicReview(Long courseId, String studentId, String reviewText, int rating) {
        validateRating(rating);

        return Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(studentId)
                .reviewText(reviewText)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Review createRatingOnlyReview(Long courseId, String studentId, int rating) {
        validateRating(rating);

        return Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(studentId)
                .reviewText("")
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Review createDetailedReview(Long courseId, String studentId, String reviewText, int rating, boolean isAnonymous) {
        validateRating(rating);

        String actualStudentId = isAnonymous ? "anonymous" : studentId;

        return Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(actualStudentId)
                .reviewText(reviewText)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating harus antara 1 and 5");
        }
    }
}