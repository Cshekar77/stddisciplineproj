package com.studentdiscipline.service;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import com.studentdiscipline.model.CaseNote;
import com.studentdiscipline.repository.CaseNoteRepository;
import com.studentdiscipline.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        Case c = caseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Case not found with id: " + id));
        
        // ✅ FIX: Ensure notes is never null
        if (c.getNotes() == null) {
            c.setNotes(new ArrayList<>());
        }
        
        return c;
    }

    public List<Case> getAllCases() {
        List<Case> cases = caseRepository.findAll();
        
        // ✅ FIX: Ensure all cases have non-null notes
        for (Case c : cases) {
            if (c.getNotes() == null) {
                c.setNotes(new ArrayList<>());
            }
        }
        
        return cases;
    }

    public List<Case> getCasesByStudentId(Long studentId) {
        List<Case> cases = caseRepository.findByStudentId(studentId);
        
        // ✅ FIX: Ensure all cases have non-null notes
        for (Case c : cases) {
            if (c.getNotes() == null) {
                c.setNotes(new ArrayList<>());
            }
        }
        
        return cases;
    }

    public List<Case> getCasesByStatus(CaseStatus status) {
        List<Case> cases = caseRepository.findByStatus(status);
        
        // ✅ FIX: Ensure all cases have non-null notes
        for (Case c : cases) {
            if (c.getNotes() == null) {
                c.setNotes(new ArrayList<>());
            }
        }
        
        return cases;
    }

    public void deleteCaseById(Long id) {
        caseRepository.deleteById(id);
    }

    public CaseNote addCaseNote(Long caseId, CaseNote note) {
        Case existingCase = getCaseById(caseId);
        note.setDisciplineCase(existingCase);
        return caseNoteRepository.save(note);
    }

    public List<CaseNote> getCaseNotes(Long caseId) {
        List<CaseNote> notes = caseNoteRepository.findByDisciplineCaseIdOrderByCreatedDateDesc(caseId);
        
        // ✅ FIX: Ensure notes is never null
        if (notes == null) {
            notes = new ArrayList<>();
        }
        
        return notes;
    }

    public Case changeCaseStatus(Long caseId, CaseStatus status) {
        Case existingCase = getCaseById(caseId);
        existingCase.setStatus(status);
        return caseRepository.save(existingCase);
    }
}