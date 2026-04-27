package com.studentdiscipline.controller;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.model.Case;
import com.studentdiscipline.model.CaseNote;
import com.studentdiscipline.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Case> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Case>> getCasesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(caseService.getCasesByStudentId(studentId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable CaseStatus status) {
        return ResponseEntity.ok(caseService.getCasesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case newCase) {
        return ResponseEntity.ok(caseService.createCase(newCase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Case> updateCase(@PathVariable Long id, @RequestBody Case updatedCase) {
        return ResponseEntity.ok(caseService.updateCase(id, updatedCase));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        caseService.deleteCaseById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{caseId}/notes")
    public ResponseEntity<CaseNote> addNote(@PathVariable Long caseId, @RequestBody CaseNote note) {
        return ResponseEntity.ok(caseService.addCaseNote(caseId, note));
    }

    @GetMapping("/{caseId}/notes")
    public ResponseEntity<List<CaseNote>> getNotes(@PathVariable Long caseId) {
        return ResponseEntity.ok(caseService.getCaseNotes(caseId));
    }

    @PatchMapping("/{caseId}/status")
    public ResponseEntity<Case> changeStatus(@PathVariable Long caseId, @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.changeCaseStatus(caseId, status));
    }
}