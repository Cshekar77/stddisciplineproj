package com.studentdiscipline.repository;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByStudentId(Long studentId);

    List<Case> findByStatus(CaseStatus status);

    Optional<Case> findByCaseNumber(String caseNumber);
}
