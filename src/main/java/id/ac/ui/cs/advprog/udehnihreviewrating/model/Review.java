package id.ac.ui.cs.advprog.udehnihreviewrating.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private Long courseId;
    private String studentId;
    private String reviewText;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}