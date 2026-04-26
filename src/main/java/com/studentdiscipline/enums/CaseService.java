package com.studentdiscipline.service;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import com.studentdiscipline.model.CaseNote;
import com.studentdiscipline.repository.CaseNoteRepository;
import com.studentdiscipline.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CaseNoteRepository caseNoteRepository;

    // ─── Create a new case ────────────────────────────────
    public Case createCase(Case disciplineCase) {
        disciplineCase.setCreatedDate(LocalDate.now());
        disciplineCase.setUpdatedDate(LocalDate.now());
        if (disciplineCase.getStatus() == null) {
            disciplineCase.setStatus(CaseStatus.OPEN);
        }
        if (disciplineCase.getCaseNumber() == null || disciplineCase.getCaseNumber().isEmpty()) {
            disciplineCase.setCaseNumber("CASE-" + System.currentTimeMillis());
        }
        return caseRepository.save(disciplineCase);
    }

    // ─── Update existing case ─────────────────────────────
    public Case updateCase(Long id, Case updatedCase) {
        Optional<Case> existing = caseRepository.findById(id);
        if (existing.isPresent()) {
            Case c = existing.get();
            c.setTitle(updatedCase.getTitle());
            c.setDescription(updatedCase.getDescription());
            c.setStatus(updatedCase.getStatus());
            c.setStudent(updatedCase.getStudent());
            c.setUpdatedDate(LocalDate.now());
            return caseRepository.save(c);
        }
        throw new RuntimeException("Case not found with id: " + id);
    }

    // ─── Get case by ID ───────────────────────────────────
    public Optional<Case> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    // ─── Get all cases ────────────────────────────────────
    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    // ─── Get cases by student ID ──────────────────────────
    public List<Case> getCasesByStudentId(Long studentId) {
        return caseRepository.findByStudentId(studentId);
    }

    // ─── Get cases by status ──────────────────────────────
    public List<Case> getCasesByStatus(CaseStatus status) {
        return caseRepository.findByStatus(status);
    }

    // ─── Delete case by ID ────────────────────────────────
    public void deleteCaseById(Long id) {
        caseRepository.deleteById(id);
    }

    // ─── Add a note to a case ─────────────────────────────
    public CaseNote addCaseNote(Long caseId, CaseNote note) {
        Case disciplineCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + caseId));
        note.setDisciplineCase(disciplineCase);
        note.setCreatedDate(LocalDateTime.now());
        return caseNoteRepository.save(note);
    }

    // ─── Get all notes for a case ─────────────────────────
    public List<CaseNote> getCaseNotes(Long caseId) {
        return caseNoteRepository.findByDisciplineCaseIdOrderByCreatedDateDesc(caseId);
    }

    // ─── Change case status ───────────────────────────────
    public Case changeCaseStatus(Long caseId, CaseStatus status) {
        Case disciplineCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + caseId));
        disciplineCase.setStatus(status);
        disciplineCase.setUpdatedDate(LocalDate.now());
        return caseRepository.save(disciplineCase);
    }
}
