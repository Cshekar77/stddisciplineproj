package com.studentdiscipline.repository;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // All feedback, newest first
    List<Feedback> findAllByOrderBySubmittedAtDesc();

    // Feedback filtered by the submitter's role
    List<Feedback> findBySubmittedBy_RoleOrderBySubmittedAtDesc(Role role);

    // Feedback submitted by a specific user
    List<Feedback> findBySubmittedBy_IdOrderBySubmittedAtDesc(Long userId);
}
