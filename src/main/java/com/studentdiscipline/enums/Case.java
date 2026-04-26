package com.studentdiscipline.model;

import com.studentdiscipline.enums.CaseStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String caseNumber;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private LocalDate createdDate;
    private LocalDate updatedDate;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "disciplineCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseNote> notes;

    // ─── Getters and Setters ───────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public LocalDate getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDate updatedDate) { this.updatedDate = updatedDate; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public List<CaseNote> getNotes() { return notes; }
    public void setNotes(List<CaseNote> notes) { this.notes = notes; }
}
