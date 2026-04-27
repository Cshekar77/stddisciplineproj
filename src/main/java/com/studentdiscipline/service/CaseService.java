package com.studentdiscipline.service;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import com.studentdiscipline.model.CaseNote;
import com.studentdiscipline.repository.CaseNoteRepository;
import com.studentdiscipline.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private CaseNoteRepository caseNoteRepository;

    public Case createCase(Case newCase) {
        return caseRepository.save(newCase);
    }

    public Case updateCase(Long id, Case updatedCase) {
        Case existing = getCaseById(id);
        existing.setTitle(updatedCase.getTitle());
        existing.setDescription(updatedCase.getDescription());
        existing.setStatus(updatedCase.getStatus());
        existing.setStudent(updatedCase.getStudent());
        return caseRepository.save(existing);
    }

    public Case getCaseById(Long id) {
        return caseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Case not found with id: " + id));
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public List<Case> getCasesByStudentId(Long studentId) {
        return caseRepository.findByStudentId(studentId);
    }

    public List<Case> getCasesByStatus(CaseStatus status) {
        return caseRepository.findByStatus(status);
    }

    public void deleteCaseById(Long id) {
        caseRepository.deleteById(id);
    }

    public CaseNote addCaseNote(Long caseId, CaseNote note) {
        Case existingCase = getCaseById(caseId);
        note.setDisciplineCase(existingCase);   // ✅ FIXED HERE
        return caseNoteRepository.save(note);
    }

    public List<CaseNote> getCaseNotes(Long caseId) {
        return caseNoteRepository.findByCaseIdOrderByCreatedDateDesc(caseId);
    }

    public Case changeCaseStatus(Long caseId, CaseStatus status) {
        Case existingCase = getCaseById(caseId);
        existingCase.setStatus(status);
        return caseRepository.save(existingCase);
    }
}