package id.ac.ui.cs.advprog.udehnihreviewrating.repository;

import id.ac.ui.cs.advprog.udehnihreviewrating.model.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findById_ExistingReview_ShouldReturnReview() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(id)
                .courseId(123L)
                .studentId(456L)
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(review);
        entityManager.flush();

        Optional<Review> foundReview = reviewRepository.findById(id);

        assertTrue(foundReview.isPresent());
        assertEquals(id, foundReview.get().getId());
        assertEquals(123L, foundReview.get().getCourseId());
        assertEquals(456L, foundReview.get().getStudentId());
        assertEquals("Great course!", foundReview.get().getReviewText());
        assertEquals(5, foundReview.get().getRating());
    }

    @Test
    void findById_NonExistingReview_ShouldReturnEmpty() {
        Optional<Review> foundReview = reviewRepository.findById(UUID.randomUUID());

        assertFalse(foundReview.isPresent());
    }

    @Test
    void findByCourseId_ExistingCourse_ShouldReturnReviews() {
        Long courseId = 123L;
        LocalDateTime now = LocalDateTime.now();

        Review review1 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(1L)
                .reviewText("Review 1")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(2L)
                .reviewText("Review 2")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Review otherCourseReview = Review.builder()
                .id(UUID.randomUUID())
                .courseId(456L)
                .studentId(3L)
                .reviewText("Other course review")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(otherCourseReview);
        entityManager.flush();

        List<Review> courseReviews = reviewRepository.findByCourseId(courseId);

        assertEquals(2, courseReviews.size());
        assertTrue(courseReviews.stream().anyMatch(r -> r.getStudentId().equals(1L)));
        assertTrue(courseReviews.stream().anyMatch(r -> r.getStudentId().equals(2L)));
        assertFalse(courseReviews.stream().anyMatch(r -> r.getStudentId().equals(3L)));
    }

    @Test
    void findByCourseId_NonExistingCourse_ShouldReturnEmptyList() {
        List<Review> courseReviews = reviewRepository.findByCourseId(999L);

        assertTrue(courseReviews.isEmpty());
    }

    @Test
    void findByStudentId_ExistingStudent_ShouldReturnReviews() {
        Long studentId = 456L;
        LocalDateTime now = LocalDateTime.now();

        Review review1 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(1L)
                .studentId(null)
                .reviewText("Course 1 review")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Review review2 = Review.builder()
                .id(UUID.randomUUID())
                .courseId(2L)
                .studentId(456L)
                .reviewText("Course 2 review")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Review otherStudentReview = Review.builder()
                .id(UUID.randomUUID())
                .courseId(1L)
                .studentId(456L)
                .reviewText("Other student review")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(otherStudentReview);
        entityManager.flush();

        List<Review> studentReviews = reviewRepository.findByStudentId(studentId);

        assertEquals(2, studentReviews.size());
        assertTrue(studentReviews.stream().anyMatch(r -> r.getCourseId().equals(1L)));
        assertTrue(studentReviews.stream().anyMatch(r -> r.getCourseId().equals(2L)));
        assertFalse(studentReviews.stream().anyMatch(r -> r.getStudentId().equals(null)));
    }

    @Test
    void findByStudentId_NonExistingStudent_ShouldReturnEmptyList() {
        List<Review> studentReviews = reviewRepository.findByStudentId(999L);

        assertTrue(studentReviews.isEmpty());
    }

    @Test
    void findByCourseIdAndStudentId_ExistingReview_ShouldReturnReview() {
        Long courseId = 123L;
        Long studentId = 456L;
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .courseId(courseId)
                .studentId(null)
                .reviewText("Great course!")
                .rating(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(review);
        entityManager.flush();

        Review foundReview = reviewRepository.findByCourseIdAndStudentId(courseId, null);

        assertNotNull(foundReview);
        assertEquals(courseId, foundReview.getCourseId());
        assertEquals(null, foundReview.getStudentId());
    }

    @Test
    void findByCourseIdAndStudentId_NonExistingCombination_ShouldReturnNull() {
        Review foundReview = reviewRepository.findByCourseIdAndStudentId(999L, 999L);

        assertNull(foundReview);
    }

    @Test
    void save_NewReview_ShouldPersistAndReturnReview() {
        UUID id = UUID.randomUUID();
        Long courseId = 123L;
        Long studentId = 456L;
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(id)
                .courseId(courseId)
                .studentId(null)
                .reviewText("New review")
                .rating(4)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Review savedReview = reviewRepository.save(review);

        assertNotNull(savedReview);
        assertEquals(id, savedReview.getId());

        Review retrievedReview = entityManager.find(Review.class, id);
        assertNotNull(retrievedReview);
        assertEquals("New review", retrievedReview.getReviewText());
    }

    @Test
    void delete_ExistingReview_ShouldRemoveReview() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .id(id)
                .courseId(123L)
                .studentId(456L)
                .reviewText("Review to delete")
                .rating(3)
                .createdAt(now)
                .updatedAt(now)
                .build();

        entityManager.persist(review);
        entityManager.flush();

        assertNotNull(entityManager.find(Review.class, id));

        reviewRepository.deleteById(id);

        assertNull(entityManager.find(Review.class, id));
    }
}