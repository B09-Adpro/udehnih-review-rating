package id.ac.ui.cs.advprog.udehnihreviewrating.repository;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByCourseId(Long courseId);
    List<Review> findByStudentId(Long studentId);
    Review findByCourseIdAndStudentId(Long courseId, Long studentId);
}