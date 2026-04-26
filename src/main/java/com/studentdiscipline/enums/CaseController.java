package com.studentdiscipline.controller;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import com.studentdiscipline.model.CaseNote;
import com.studentdiscipline.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    // ─── GET all cases — Admin only ───────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Case>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    // ─── GET case by ID — Admin and Teacher ───────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Case> getCaseById(@PathVariable Long id) {
        return caseService.getCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── GET cases by student ID — Admin and Teacher ──────
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Case>> getCasesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(caseService.getCasesByStudentId(studentId));
    }

    // ─── GET cases by status — Admin and Teacher ──────────
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable CaseStatus status) {
        return ResponseEntity.ok(caseService.getCasesByStatus(status));
    }

    // ─── POST create case — Admin and Teacher ─────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Case> createCase(@RequestBody Case disciplineCase) {
        return ResponseEntity.ok(caseService.createCase(disciplineCase));
    }

    // ─── PUT update case — Admin and Teacher ──────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Case> updateCase(@PathVariable Long id,
                                            @RequestBody Case disciplineCase) {
        return ResponseEntity.ok(caseService.updateCase(id, disciplineCase));
    }

    // ─── PATCH change status — Admin only ─────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Case> changeStatus(@PathVariable Long id,
                                              @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.changeCaseStatus(id, status));
    }

    // ─── DELETE case — Admin only ─────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        caseService.deleteCaseById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── POST add note to case — Admin and Teacher ────────
    @PostMapping("/{caseId}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CaseNote> addNote(@PathVariable Long caseId,
                                             @RequestBody CaseNote note) {
        return ResponseEntity.ok(caseService.addCaseNote(caseId, note));
    }

    // ─── GET notes for a case — Admin and Teacher ─────────
    @GetMapping("/{caseId}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<CaseNote>> getNotes(@PathVariable Long caseId) {
        return ResponseEntity.ok(caseService.getCaseNotes(caseId));
    }
}
