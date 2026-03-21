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

    // Create teacher with 5 parameters - auto-generates employeeId
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

    // Create teacher with 8 parameters
    public Teacher createTeacher(String firstName, String lastName, String employeeId,
                                  String department, String email, String contactNumber,
                                  String username, String password) {
        if (teacherRepository.existsByEmployeeId(employeeId)) {
            throw new RuntimeException("Employee ID already exists: " + employeeId);
        }

        User user = userService.createUser(username, password, Role.TEACHER);
        user.setFirstName(firstName);
        user.setLastName(lastName);

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

    public List<Teacher> getAllTeachers() { return teacherRepository.findAll(); }
    public long countAll() { return teacherRepository.count(); }
    public List<Teacher> findAll() { return teacherRepository.findAll(); }
    public Optional<Teacher> getTeacherById(Long id) { return teacherRepository.findById(id); }
    public Optional<Teacher> findById(Long id) { return teacherRepository.findById(id); }
    public Optional<Teacher> getTeacherByEmployeeId(String employeeId) { return teacherRepository.findByEmployeeId(employeeId); }
    public Optional<Teacher> getTeacherByUser(User user) { return teacherRepository.findByUser(user); }
    public List<Teacher> getTeachersByDepartment(String department) { return teacherRepository.findByDepartment(department); }

    public List<Teacher> search(String term) {
        String searchTerm = term.toLowerCase();
        return findAll().stream()
                .filter(t -> t.getFirstName().toLowerCase().contains(searchTerm)
                        || t.getLastName().toLowerCase().contains(searchTerm)
                        || (t.getDepartment() != null && t.getDepartment().toLowerCase().contains(searchTerm)))
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
        teacher.setDepartment(department);
        teacher.setEmail(email);
        teacher.setContactNumber(contactNumber);
        return teacherRepository.save(teacher);
    }

    public Teacher update(Long id, String firstName, String lastName, String department) {
        return updateTeacher(id, firstName, lastName, department, null, null);
    }

    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public void deleteById(Long id) { deleteTeacher(id); }
}