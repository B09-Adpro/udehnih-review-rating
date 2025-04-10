package id.ac.ui.cs.advprog.udehnihreviewrating.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    private UUID id;
    private String courseId;
    private String studentId;
    private String reviewText;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}