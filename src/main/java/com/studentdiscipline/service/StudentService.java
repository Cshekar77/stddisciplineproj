package com.studentdiscipline.service;

import com.studentdiscipline.enums.DisciplineStatus;
import com.studentdiscipline.enums.IncidentStatus;
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

    // ── Create student WITHOUT credentials (credentials set separately) ────────
    public Student createStudentNoCredentials(String firstName, String lastName,
                                               String studentId, String grade, String section) {
        if (studentRepository.existsByStudentId(studentId))
            throw new RuntimeException("Student ID already exists: " + studentId);

        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setStudentId(studentId);
        student.setGrade(grade);
        student.setSection(section);
        student.setDateEnrolled(LocalDate.now());
        student.setDisciplineStatus(DisciplineStatus.GOOD);
        student.setUser(null); // no account yet — set via Credentials page
        return studentRepository.save(student);
    }

    // ── Create student WITH credentials (used internally or via old flow) ──────
    public Student createStudent(String firstName, String lastName, String studentId,
                                  String grade, String section, String email, String password) {
        if (studentRepository.existsByStudentId(studentId))
            throw new RuntimeException("Student ID already exists: " + studentId);

        User user = userService.createUser(email, password,
                com.studentdiscipline.enums.Role.STUDENT);
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

    // ── Save/update directly ──────────────────────────────────────────────────
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // ── Discipline status ─────────────────────────────────────────────────────
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
        if (grade != null) student.setGrade(grade);
        if (section != null) student.setSection(section);
        if (email != null) student.setEmail(email);
        return studentRepository.save(student);
    }

    public Student update(Long id, String firstName, String lastName, String grade, String section) {
        return updateStudent(id, firstName, lastName, grade, section, null);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id))
            throw new RuntimeException("Student not found with id: " + id);
        studentRepository.deleteById(id);
    }

    public void deleteById(Long id) { deleteStudent(id); }
}