package id.ac.ui.cs.advprog.udehnihreviewrating.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private Long courseId;
    private String reviewText;
    private int rating;
    private boolean anonymous;
}