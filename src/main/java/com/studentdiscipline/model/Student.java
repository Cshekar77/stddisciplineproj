package com.studentdiscipline.model;

import com.studentdiscipline.enums.DisciplineStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String studentId;

    @Column(nullable = false)
    private String grade;

    @Column(nullable = false)
    private String section;

    private String email;

    private LocalDate dateEnrolled;

    @Enumerated(EnumType.STRING)
    private DisciplineStatus disciplineStatus = DisciplineStatus.GOOD;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // ✅ ADDED: One student can have many cases
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Case> cases = new ArrayList<>();

    // ✅ ADDED: Teacher relationship (CRITICAL FIX)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Constructors
    public Student() {}

    public Student(String firstName, String lastName, String studentId, String grade, String section) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.grade = grade;
        this.section = section;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateEnrolled() { return dateEnrolled; }
    public void setDateEnrolled(LocalDate dateEnrolled) { this.dateEnrolled = dateEnrolled; }

    public DisciplineStatus getDisciplineStatus() { return disciplineStatus; }
    public void setDisciplineStatus(DisciplineStatus disciplineStatus) { this.disciplineStatus = disciplineStatus; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // ✅ ADDED: Getter & Setter for cases
    public List<Case> getCases() { return cases; }
    public void setCases(List<Case> cases) { this.cases = cases; }

    // ✅ ADDED: Getter & Setter for teacher
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    // Helper
    public String getFullName() { return firstName + " " + lastName; }
}