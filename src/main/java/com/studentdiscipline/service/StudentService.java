package com.studentdiscipline.service;

import com.studentdiscipline.enums.DisciplineStatus;
import com.studentdiscipline.enums.IncidentStatus;
import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Incident;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.IncidentRepository;
import com.studentdiscipline.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private UserService userService;
    @Autowired private IncidentRepository incidentRepository;

    public Student createStudent(String firstName, String lastName, String studentId,
                                  String grade, String section, String email, String password) {
        if (studentRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already exists: " + studentId);
        }
        User user = userService.createUser(email, password, Role.STUDENT);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setSection(section);
        student.setEmail(email);
        student.setDateEnrolled(LocalDate.now());
        student.setDisciplineStatus(DisciplineStatus.GOOD);
        student.setUser(user);
        return studentRepository.save(student);
    }

    public Student createStudent(String firstName, String lastName, String studentId,
                                  String grade, String section, String email,
                                  String username, String password) {
        if (studentRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already exists: " + studentId);
        }
        User user = userService.createUser(username, password, Role.STUDENT);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setSection(section);
        student.setEmail(email);
        student.setDateEnrolled(LocalDate.now());
        student.setDisciplineStatus(DisciplineStatus.GOOD);
        student.setUser(user);
        return studentRepository.save(student);
    }

    // Save/update a student directly (used by credentials endpoint)
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public DisciplineStatus calculateDisciplineStatus(Student student) {
        List<Incident> incidents = incidentRepository.findByStudent(student);
        long open = incidents.stream().filter(i -> i.getStatus() == IncidentStatus.OPEN).count();
        if (open == 0) return DisciplineStatus.GOOD;
        else if (open <= 2) return DisciplineStatus.WARNING;
        else return DisciplineStatus.CRITICAL;
    }

    public Student updateDisciplineStatus(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setDisciplineStatus(calculateDisciplineStatus(student));
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() { return studentRepository.findAll(); }
    public long countAll() { return studentRepository.count(); }
    public List<Student> findAll() { return studentRepository.findAll(); }
    public Optional<Student> getStudentById(Long id) { return studentRepository.findById(id); }
    public Optional<Student> findById(Long id) { return studentRepository.findById(id); }
    public Optional<Student> getStudentByStudentId(String studentId) { return studentRepository.findByStudentId(studentId); }
    public Optional<Student> getStudentByUser(User user) { return studentRepository.findByUser(user); }
    public List<Student> getStudentsByGrade(String grade) { return studentRepository.findByGrade(grade); }

    public List<Student> search(String term) {
        String s = term.toLowerCase();
        return findAll().stream()
                .filter(st -> st.getFirstName().toLowerCase().contains(s)
                        || st.getLastName().toLowerCase().contains(s)
                        || st.getStudentId().toLowerCase().contains(s))
                .collect(Collectors.toList());
    }

    public List<Student> findByGradeLevel(String gradeLevel) { return studentRepository.findByGrade(gradeLevel); }

    public List<String> getAllGradeLevels() {
        return findAll().stream().map(Student::getGrade).filter(Objects::nonNull)
                .distinct().sorted().collect(Collectors.toList());
    }

    public List<Student> getStudentsBySection(String section) { return studentRepository.findBySection(section); }
    public List<Student> getStudentsByGradeAndSection(String grade, String section) { return studentRepository.findByGradeAndSection(grade, section); }
    public List<Student> searchStudentsByLastName(String lastName) { return studentRepository.findByLastNameContainingIgnoreCase(lastName); }

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

    public Student update(Long id, String firstName, String lastName, String gradeLevel, String section) {
        return updateStudent(id, firstName, lastName, gradeLevel, section, null);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public void deleteById(Long id) { deleteStudent(id); }
}