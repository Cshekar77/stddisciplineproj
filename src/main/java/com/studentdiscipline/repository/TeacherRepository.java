package com.studentdiscipline.repository;

import com.studentdiscipline.model.Teacher;
import com.studentdiscipline.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmployeeId(String employeeId);

    Optional<Teacher> findByUser(User user);

    boolean existsByEmployeeId(String employeeId);

    List<Teacher> findByDepartment(String department);

    List<Teacher> findByLastNameContainingIgnoreCase(String lastName);
}
