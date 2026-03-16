package com.studentdiscipline.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who submitted the feedback (Teacher or Student)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;

    // Optional: feedback can be linked to a specific incident
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @Column(nullable = false, length = 1000)
    private String message;

    // e.g. "GENERAL", "INCIDENT_RELATED", "SYSTEM"
    @Column(nullable = false)
    private String category;

    // e.g. "PENDING", "REVIEWED", "DISMISSED"
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Admin who reviewed the feedback
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "admin_notes", length = 500)
    private String adminNotes;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    public Feedback() {}

    public Feedback(User submittedBy, String message, String category) {
        this.submittedBy = submittedBy;
        this.message = message;
        this.category = category;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }

    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // NEWLY ADDED: setContent() - Alias for setMessage() (Required by FeedbackService)
    public void setContent(String content) { this.message = content; }

    // NEWLY ADDED: getContent() - Alias for getMessage() (Required by FeedbackService)
    public String getContent() { return this.message; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}