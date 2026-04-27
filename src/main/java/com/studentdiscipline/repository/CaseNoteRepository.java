package com.studentdiscipline.repository;

import com.studentdiscipline.model.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {
    List<CaseNote> findByCaseIdOrderByCreatedDateDesc(Long caseId);
}