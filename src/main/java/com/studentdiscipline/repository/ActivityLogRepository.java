package com.studentdiscipline.repository;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // All logs, newest first
    List<ActivityLog> findAllByOrderByTimestampDesc();

    // Logs for a specific user (their own activity)
    List<ActivityLog> findByUser_IdOrderByTimestampDesc(Long userId);

    // Logs filtered by user role
    List<ActivityLog> findByUser_RoleOrderByTimestampDesc(Role role);

    // Most recent N logs (used for dashboard preview)
    List<ActivityLog> findTop10ByOrderByTimestampDesc();

    // Search by description keyword
    @Query("SELECT a FROM ActivityLog a WHERE LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY a.timestamp DESC")
    List<ActivityLog> searchByDescription(@Param("keyword") String keyword);

    // Search by keyword filtered by role
    @Query("SELECT a FROM ActivityLog a WHERE a.user.role = :role AND LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY a.timestamp DESC")
    List<ActivityLog> searchByDescriptionAndRole(@Param("keyword") String keyword, @Param("role") Role role);
}
