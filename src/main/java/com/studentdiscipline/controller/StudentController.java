package com.studentdiscipline.controller;

import com.studentdiscipline.model.*;
import com.studentdiscipline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private IncidentService incidentService;
    @Autowired private SanctionService sanctionService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private AnonymousReportService anonymousReportService;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private UserService userService;

    private User currentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Student currentStudent(UserDetails userDetails) {
        User user = currentUser(userDetails);
        return studentService.getStudentByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Student student = currentStudent(userDetails);
            model.addAttribute("student", student);
            model.addAttribute("studentName", student.getFullName());
            model.addAttribute("disciplineStatus", student.getDisciplineStatus());
            model.addAttribute("incidentCount",
                    incidentService.getIncidentsByStudent(student).size());
            model.addAttribute("sanctionCount",
                    sanctionService.getSanctionsByStudent(student).size());
            model.addAttribute("recentIncidents",
                    incidentService.getIncidentsByStudent(student)
                            .stream().limit(3).collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            model.addAttribute("studentName", "Student");
            model.addAttribute("disciplineStatus", null);
            model.addAttribute("incidentCount", 0);
            model.addAttribute("sanctionCount", 0);
            model.addAttribute("recentIncidents", new ArrayList<>());
        }
        return "student/dashboard";
    }

    @GetMapping("/my-record")
    public String record(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Student student = currentStudent(userDetails);
            model.addAttribute("student", student);
            model.addAttribute("incidents", incidentService.getIncidentsByStudent(student));
            model.addAttribute("disciplineStatus", student.getDisciplineStatus());
        } catch (Exception e) {
            model.addAttribute("incidents", new ArrayList<>());
        }
        return "student/my-record";
    }

    @GetMapping("/my-sanctions")
    public String sanctions(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Student student = currentStudent(userDetails);
            model.addAttribute("student", student);
            model.addAttribute("sanctions", sanctionService.getSanctionsByStudent(student));
        } catch (Exception e) {
            model.addAttribute("sanctions", new ArrayList<>());
        }
        return "student/my-sanctions";
    }

    @GetMapping("/feedback")
    public String feedback(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = currentUser(userDetails);
            model.addAttribute("feedbackList", feedbackService.getFeedbackByUser(user.getId()));
        } catch (Exception e) {
            model.addAttribute("feedbackList", new ArrayList<>());
        }
        return "student/feedback";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@RequestParam String message,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        try {
            User user = currentUser(userDetails);
            feedbackService.submitFeedback(message, user);
            ra.addFlashAttribute("success", "Feedback submitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/feedback";
    }

    @GetMapping("/anonymous-report")
    public String anonymousReport() {
        return "student/anonymous-report";
    }

    @PostMapping("/anonymous-report/submit")
    public String submitReport(@RequestParam String description,
                               @RequestParam(required = false) String reportedAgainst,
                               RedirectAttributes ra) {
        try {
            AnonymousReport report = new AnonymousReport();
            report.setDescription(description);
            report.setReportedAgainst(reportedAgainst);
            anonymousReportService.submitReport(report);
            ra.addFlashAttribute("success", "Anonymous report submitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/anonymous-report";
    }

    @GetMapping("/activity")
    public String activity(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = currentUser(userDetails);
            model.addAttribute("logs", activityLogService.getLogsByUser(user.getId()));
        } catch (Exception e) {
            model.addAttribute("logs", new ArrayList<>());
        }
        return "student/activity-history";
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "student/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/student/change-password";
        }

        if (newPassword.length() < 6) {
            ra.addFlashAttribute("error", "New password must be at least 6 characters.");
            return "redirect:/student/change-password";
        }

        boolean success = userService.changePassword(userDetails.getUsername(), currentPassword, newPassword);

        if (!success) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/student/change-password";
        }

        activityLogService.log(currentUser(userDetails), "Student changed their own password.");
        ra.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/student/change-password";
    }
}