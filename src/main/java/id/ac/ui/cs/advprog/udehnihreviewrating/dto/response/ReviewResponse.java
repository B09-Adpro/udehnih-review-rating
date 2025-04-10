package id.ac.ui.cs.advprog.udehnihreviewrating.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private String courseId;
    private String courseName;
    private String studentId;
    private String studentName;
    private String reviewText;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isAnonymous;
}