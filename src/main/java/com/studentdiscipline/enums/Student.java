package com.studentdiscipline.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String studentNumber;
    private String grade;
    private String section;
    private LocalDate enrollmentDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ── CASE MANAGEMENT (Member 1 addition) ──────────────
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Case> cases;
    // ─────────────────────────────────────────────────────

    // ─── Getters and Setters ───────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ── CASE MANAGEMENT getter/setter (Member 1 addition) ─
    public List<Case> getCases() { return cases; }
    public void setCases(List<Case> cases) { this.cases = cases; }
    // ─────────────────────────────────────────────────────
}
