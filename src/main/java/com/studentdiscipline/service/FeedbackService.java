package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Feedback;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ActivityLogService activityLogService;

    // ── Create ─────────────────────────────────────────────────────────────────

    /**
     * Submit new feedback from a user (teacher or student).
     */
    public Feedback submitFeedback(String content, String category, User submittedBy) {
        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setCategory(category);
        feedback.setSubmittedBy(submittedBy);
        Feedback saved = feedbackRepository.save(feedback);

        activityLogService.log(
            submittedBy,
            submittedBy.getFirstName() + " " + submittedBy.getLastName()
                + " submitted feedback (Category: " + category + ")"
        );

        return saved;
    }

    // ── Read ───────────────────────────────────────────────────────────────────

    /**
     * Get all feedback, newest first. Used by admin.
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderBySubmittedAtDesc();
    }

    /**
     * Get feedback filtered by the submitter's role (TEACHER or STUDENT).
     */
    public List<Feedback> getFeedbackByRole(String roleStr) {
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            return feedbackRepository.findBySubmittedBy_RoleOrderBySubmittedAtDesc(role);
        } catch (IllegalArgumentException e) {
            return getAllFeedback();
        }
    }

    /**
     * Get all feedback submitted by a specific user. Used by the submitter.
     */
    public List<Feedback> getFeedbackByUser(Long userId) {
        return feedbackRepository.findBySubmittedBy_IdOrderBySubmittedAtDesc(userId);
    }

    /**
     * Find a single feedback entry by ID.
     */
    public Optional<Feedback> getById(Long id) {
        return feedbackRepository.findById(id);
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    /**
     * Delete a feedback entry by ID.
     */
    public void deleteFeedback(Long id, User deletedBy) {
        feedbackRepository.deleteById(id);

        activityLogService.log(
            deletedBy,
            deletedBy.getFirstName() + " " + deletedBy.getLastName()
                + " deleted feedback ID " + id + "."
        );
    }
}