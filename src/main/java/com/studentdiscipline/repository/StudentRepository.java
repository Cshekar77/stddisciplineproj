package com.studentdiscipline.repository;

import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentId(String studentId);

    Optional<Student> findByUser(User user);

    boolean existsByStudentId(String studentId);

    List<Student> findByGrade(String grade);

    List<Student> findBySection(String section);

    List<Student> findByGradeAndSection(String grade, String section);

    List<Student> findByLastNameContainingIgnoreCase(String lastName);
}
