package com.studentdiscipline.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Helper
    public String getFullName() { return firstName + " " + lastName; }
}
