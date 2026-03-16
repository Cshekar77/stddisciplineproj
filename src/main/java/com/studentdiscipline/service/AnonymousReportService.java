package com.studentdiscipline.service;

import com.studentdiscipline.model.AnonymousReport;
import com.studentdiscipline.repository.AnonymousReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnonymousReportService {

    private final AnonymousReportRepository anonymousReportRepository;

    public List<AnonymousReport> getAllReports() {
        return anonymousReportRepository.findAll();
    }

    // NEWLY ADDED: Get all reports (Alias for getAllReports - Required by AdminController)
    public List<AnonymousReport> findAll() {
        return anonymousReportRepository.findAll();
    }

    public List<AnonymousReport> getUnreviewedReports() {
        return anonymousReportRepository.findByReviewed(false);
    }

    public AnonymousReport submitReport(AnonymousReport report) {
        report.setSubmittedAt(LocalDateTime.now());
        report.setReviewed(false);
        return anonymousReportRepository.save(report);
    }

    public AnonymousReport markAsReviewed(Long id) {
        AnonymousReport report = anonymousReportRepository.findById(id).orElse(null);
        if (report != null) {
            report.setReviewed(true);
            return anonymousReportRepository.save(report);
        }
        return null;
    }

    // NEWLY ADDED: Mark report as reviewed (Alias for markAsReviewed - Required by AdminController)
    public void markReviewed(Long id) {
        Optional<AnonymousReport> reportOpt = anonymousReportRepository.findById(id);
        if (reportOpt.isPresent()) {
            AnonymousReport report = reportOpt.get();
            report.setReviewed(true);
            anonymousReportRepository.save(report);
        }
    }

    public void deleteReport(Long id) {
        anonymousReportRepository.deleteById(id);
    }

    // NEWLY ADDED: Delete report by ID (Alias for deleteReport - Required by AdminController)
    public void deleteById(Long id) {
        deleteReport(id);
    }
}