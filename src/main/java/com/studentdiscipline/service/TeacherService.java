package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Teacher;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired private TeacherRepository teacherRepository;
    @Autowired private UserService userService;

    // ── Create teacher WITHOUT credentials (credentials set separately) ────────
    public Teacher createTeacherNoCredentials(String firstName, String lastName,
                                               String department, String email) {
        String employeeId = "EMP-" + System.currentTimeMillis();
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmployeeId(employeeId);
        teacher.setDepartment(department != null && !department.isBlank() ? department : "General");
        teacher.setEmail(email);
        teacher.setUser(null); // no account yet — set via Credentials page
        return teacherRepository.save(teacher);
    }

    // ── Create teacher WITH credentials (used internally or via old flow) ──────
    public Teacher createTeacher(String firstName, String lastName, String email,
                                  String department, String password) {
        String employeeId = "EMP-" + System.currentTimeMillis();
        User user = userService.createUser(email, password, Role.TEACHER);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmployeeId(employeeId);
        teacher.setDepartment(department != null && !department.isBlank() ? department : "General");
        teacher.setEmail(email);
        teacher.setUser(user);
        return teacherRepository.save(teacher);
    }

    // ── Save/update directly ──────────────────────────────────────────────────
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> getAllTeachers() { return teacherRepository.findAll(); }
    public long countAll() { return teacherRepository.count(); }
    public List<Teacher> findAll() { return teacherRepository.findAll(); }
    public Optional<Teacher> getTeacherById(Long id) { return teacherRepository.findById(id); }
    public Optional<Teacher> findById(Long id) { return teacherRepository.findById(id); }
    public Optional<Teacher> getTeacherByEmployeeId(String employeeId) { return teacherRepository.findByEmployeeId(employeeId); }
    public Optional<Teacher> getTeacherByUser(User user) { return teacherRepository.findByUser(user); }
    public List<Teacher> getTeachersByDepartment(String department) { return teacherRepository.findByDepartment(department); }

    public List<Teacher> search(String term) {
        String s = term.toLowerCase();
        return findAll().stream()
                .filter(t -> t.getFirstName().toLowerCase().contains(s)
                        || t.getLastName().toLowerCase().contains(s)
                        || (t.getDepartment() != null && t.getDepartment().toLowerCase().contains(s)))
                .collect(Collectors.toList());
    }

    public List<Teacher> searchTeachersByLastName(String lastName) {
        return teacherRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    public Teacher updateTeacher(Long id, String firstName, String lastName,
                                  String department, String email, String contactNumber) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        if (department != null) teacher.setDepartment(department);
        if (email != null) teacher.setEmail(email);
        if (contactNumber != null) teacher.setContactNumber(contactNumber);
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, String firstName, String lastName, String department) {
        return updateTeacher(id, firstName, lastName, department, null, null);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id))
            throw new RuntimeException("Teacher not found with id: " + id);
        teacherRepository.deleteById(id);
    }

    public void deleteById(Long id) { deleteTeacher(id); }
}