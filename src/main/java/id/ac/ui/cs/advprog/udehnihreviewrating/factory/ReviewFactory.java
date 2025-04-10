package id.ac.ui.cs.advprog.udehnihreviewrating.factory;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;

public interface ReviewFactory {
    Review createBasicReview(String courseId, String studentId, String reviewText, int rating);
    Review createRatingOnlyReview(String courseId, String studentId, int rating);
    Review createDetailedReview(String courseId, String studentId, String reviewText, int rating, boolean isAnonymous);
}
