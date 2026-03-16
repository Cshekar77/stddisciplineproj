package com.studentdiscipline.service;

import com.studentdiscipline.model.AnonymousReport;
import com.studentdiscipline.repository.AnonymousReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnonymousReportService {

    private final AnonymousReportRepository anonymousReportRepository;

    public List<AnonymousReport> getAllReports() {
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

    public void deleteReport(Long id) {
        anonymousReportRepository.deleteById(id);
    }
}