package com.studentdiscipline.repository;

import com.studentdiscipline.model.Sanction;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {

    // Get all sanctions for a specific student
    List<Sanction> findByStudent(Student student);

    // Get all sanctions for a specific incident
    List<Sanction> findByIncident(Incident incident);
}