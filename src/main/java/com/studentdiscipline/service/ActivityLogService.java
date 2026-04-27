package com.studentdiscipline.service;

import com.studentdiscipline.enums.Role;
import com.studentdiscipline.model.ActivityLog;
import com.studentdiscipline.model.User;
import com.studentdiscipline.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    // ── Create ─────────────────────────────────────────────────────────────────

    /**
     * Log an action performed by a user.
     * Call this from any other service after a significant action.
     *
     * Example:
     *   activityLogService.log(currentUser, "created incident for student John Doe.");
     */
    public ActivityLog log(User user, String description) {
        ActivityLog entry = new ActivityLog();
        
        // ✅ FIX: Ensure user is properly set and handle null cases
        if (user != null) {
            entry.setUser(user);
            // ✅ FIX: Prepend user name to description for better display
            String firstName = user.getFirstName() != null ? user.getFirstName() : "Unknown";
            String lastName = user.getLastName() != null ? user.getLastName() : "User";
            String userName = firstName + " " + lastName;
            entry.setDescription(userName + " " + description);
        } else {
            entry.setUser(null);
            entry.setDescription("[SYSTEM] " + description);
        }
        
        entry.setTimestamp(LocalDateTime.now());
        return activityLogRepository.save(entry);
    }

    /**
     * Log a system-level event with no associated user (e.g. startup tasks).
     */
    public ActivityLog logSystem(String description) {
        ActivityLog entry = new ActivityLog();
        entry.setUser(null);
        entry.setDescription("[SYSTEM] " + description);
        entry.setTimestamp(LocalDateTime.now());
        return activityLogRepository.save(entry);
    }

    // ── Read ───────────────────────────────────────────────────────────────────

    /**
     * All logs, newest first. Used by admin activity-history page.
     */
    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Last 10 logs — used on the admin dashboard preview widget.
     */
    public List<ActivityLog> getRecentLogs() {
        return activityLogRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * All logs for a single user — used by teacher/student own activity page.
     */
    public List<ActivityLog> getLogsByUser(Long userId) {
        return activityLogRepository.findByUser_IdOrderByTimestampDesc(userId);
    }

    /**
     * All logs filtered by role.
     */
    public List<ActivityLog> getLogsByRole(String roleStr) {
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            return activityLogRepository.findByUser_RoleOrderByTimestampDesc(role);
        } catch (IllegalArgumentException e) {
            return getAllLogs();
        }
    }

    /**
     * Search all logs by a keyword in the description.
     */
    public List<ActivityLog> searchLogs(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllLogs();
        }
        return activityLogRepository.searchByDescription(keyword.trim());
    }

    /**
     * Search logs by keyword AND role together.
     */
    public List<ActivityLog> searchLogsByKeywordAndRole(String keyword, String roleStr) {
        if (keyword == null || keyword.isBlank()) {
            return getLogsByRole(roleStr);
        }
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            return activityLogRepository.searchByDescriptionAndRole(keyword.trim(), role);
        } catch (IllegalArgumentException e) {
            return searchLogs(keyword);
        }
    }

    /**
     * Find a single log entry by ID.
     */
    public Optional<ActivityLog> getById(Long id) {
        return activityLogRepository.findById(id);
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    /**
     * Delete a specific log entry (admin use only).
     */
    public void deleteLog(Long id) {
        activityLogRepository.deleteById(id);
    }
}