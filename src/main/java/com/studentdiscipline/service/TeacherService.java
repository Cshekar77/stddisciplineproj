package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.Teacher;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    // Get teacher by DB id
    public Optional<Teacher> getTeacherById(Long id) {
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

    // Delete teacher
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }
}
