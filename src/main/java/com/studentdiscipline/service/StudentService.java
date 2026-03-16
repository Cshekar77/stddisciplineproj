package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    // Create student and linked user account
    public Student createStudent(String firstName, String lastName, String studentId,
                                  String grade, String section, String email, String username, String password) {
        if (studentRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already exists: " + studentId);
        }

        // Create linked user account
        User user = userService.createUser(username, password, Role.STUDENT);

        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setSection(section);
        student.setEmail(email);
        student.setDateEnrolled(LocalDate.now());
        student.setUser(user);

        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by DB id
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Get student by studentId
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    // Get student by linked user
    public Optional<Student> getStudentByUser(User user) {
        return studentRepository.findByUser(user);
    }

    // Get students by grade
    public List<Student> getStudentsByGrade(String grade) {
        return studentRepository.findByGrade(grade);
    }

    // Get students by section
    public List<Student> getStudentsBySection(String section) {
        return studentRepository.findBySection(section);
    }

    // Get students by grade and section
    public List<Student> getStudentsByGradeAndSection(String grade, String section) {
        return studentRepository.findByGradeAndSection(grade, section);
    }

    // Search students by last name
    public List<Student> searchStudentsByLastName(String lastName) {
        return studentRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    // Update student details
    public Student updateStudent(Long id, String firstName, String lastName,
                                  String grade, String section, String email) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGrade(grade);
        student.setSection(section);
        student.setEmail(email);
        return studentRepository.save(student);
    }

    // Delete student
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}
