package com.studentdiscipline.repository;

import com.studentdiscipline.model.Incident;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // Get all incidents for a specific student
    List<Incident> findByStudent(Student student);

    // Get all incidents reported by a specific teacher
    List<Incident> findByTeacher(Teacher teacher);
}