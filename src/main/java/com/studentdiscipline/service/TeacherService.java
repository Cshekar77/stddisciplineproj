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

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserService userService;

    // Create teacher and linked user account
    public Teacher createTeacher(String firstName, String lastName, String employeeId,
                                  String department, String email, String contactNumber,
                                  String username, String password) {
        if (teacherRepository.existsByEmployeeId(employeeId)) {
            throw new RuntimeException("Employee ID already exists: " + employeeId);
        }

        // Create linked user account
        User user = userService.createUser(username, password, Role.TEACHER);

        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmployeeId(employeeId);
        teacher.setDepartment(department);
        teacher.setEmail(email);
        teacher.setContactNumber(contactNumber);
        teacher.setUser(user);

        return teacherRepository.save(teacher);
    }

    // Get all teachers
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // NEWLY ADDED: Count total teachers (Required by AdminController)
    public long countAll() {
        return teacherRepository.count();
    }

    // NEWLY ADDED: Get all teachers (Alias for getAllTeachers - Required by AdminController)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    // Get teacher by DB id
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    // NEWLY ADDED: Find teacher by ID (Alias for getTeacherById - Required by AdminController)
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    // Get teacher by employeeId
    public Optional<Teacher> getTeacherByEmployeeId(String employeeId) {
        return teacherRepository.findByEmployeeId(employeeId);
    }

    // Get teacher by linked user
    public Optional<Teacher> getTeacherByUser(User user) {
        return teacherRepository.findByUser(user);
    }

    // Get teachers by department
    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    // NEWLY ADDED: Search teachers by name or department (Required by AdminController)
    public List<Teacher> search(String term) {
        String searchTerm = term.toLowerCase();
        return findAll().stream()
                .filter(t -> t.getFirstName().toLowerCase().contains(searchTerm)
                        || t.getLastName().toLowerCase().contains(searchTerm)
                        || (t.getDepartment() != null && t.getDepartment().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }

    // Search teachers by last name
    public List<Teacher> searchTeachersByLastName(String lastName) {
        return teacherRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    // Update teacher details
    public Teacher updateTeacher(Long id, String firstName, String lastName,
                                  String department, String email, String contactNumber) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setDepartment(department);
        teacher.setEmail(email);
        teacher.setContactNumber(contactNumber);
        return teacherRepository.save(teacher);
    }

    // NEWLY ADDED: Update teacher (Alias for updateTeacher - Required by AdminController)
    public Teacher update(Long id, String firstName, String lastName, String department) {
        return updateTeacher(id, firstName, lastName, department, null, null);
    }

    // Delete teacher
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    // NEWLY ADDED: Delete teacher by ID (Alias for deleteTeacher - Required by AdminController)
    public void deleteById(Long id) {
        deleteTeacher(id);
    }
}