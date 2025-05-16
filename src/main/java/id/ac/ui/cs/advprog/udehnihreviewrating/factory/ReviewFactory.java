package id.ac.ui.cs.advprog.udehnihreviewrating.factory;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;

public interface ReviewFactory {
    Review createBasicReview(Long courseId, Long studentId, String reviewText, int rating);
    Review createRatingOnlyReview(Long courseId, Long studentId, int rating);
    Review createDetailedReview(Long courseId, Long studentId, String reviewText, int rating, boolean isAnonymous);
}