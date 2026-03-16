package com.studentdiscipline.repository;

import com.studentdiscipline.model.AnonymousReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnonymousReportRepository extends JpaRepository<AnonymousReport, Long> {

    // Get all unreviewed anonymous reports
    List<AnonymousReport> findByReviewed(boolean reviewed);
}