package com.studentdiscipline.controller;

import com.studentdiscipline.enums.IncidentStatus;
import com.studentdiscipline.enums.IncidentType;
import com.studentdiscipline.enums.Role;
import com.studentdiscipline.enums.SanctionType;
import com.studentdiscipline.model.*;
import com.studentdiscipline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private StudentService studentService;
    @Autowired private TeacherService teacherService;
    @Autowired private IncidentService incidentService;
    @Autowired private SanctionService sanctionService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private AnonymousReportService anonymousReportService;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private UserService userService;

    // ── Helper: get current logged-in User entity ──────────────────────────────
    private User currentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User admin = currentUser(userDetails);

        model.addAttribute("adminName", admin.getFirstName() + " " + admin.getLastName());
        model.addAttribute("totalStudents",    studentService.countAll());
        model.addAttribute("totalTeachers",    teacherService.countAll());
        model.addAttribute("openIncidents",    incidentService.countByStatus(IncidentStatus.OPEN));
        model.addAttribute("resolvedIncidents",incidentService.countByStatus(IncidentStatus.RESOLVED));
        model.addAttribute("recentIncidents",  incidentService.getRecent(5));
        model.addAttribute("recentActivity",   activityLogService.getRecentLogs());

        return "admin/dashboard";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TEACHERS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/teachers")
    public String teachers(@RequestParam(required = false) String search, Model model) {
        List<Teacher> teachers = (search != null && !search.isBlank())
                ? teacherService.search(search)
                : teacherService.findAll();
        model.addAttribute("teachers", teachers);
        model.addAttribute("search", search);
        return "admin/teachers";
    }

    @PostMapping("/teachers/add")
    public String addTeacher(@RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam(required = false) String subject,
                             @RequestParam String password,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes ra) {
        teacherService.createTeacher(firstName, lastName, email, subject, password);
        activityLogService.log(currentUser(userDetails),
                "Admin added teacher: " + firstName + " " + lastName);
        ra.addFlashAttribute("success", "Teacher added successfully.");
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/edit/{id}")
    public String editTeacherForm(@PathVariable Long id, Model model) {
        teacherService.findById(id).ifPresent(t -> model.addAttribute("teacher", t));
        return "admin/teachers";
    }

    @PostMapping("/teachers/edit/{id}")
    public String editTeacher(@PathVariable Long id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam(required = false) String subject,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes ra) {
        teacherService.update(id, firstName, lastName, subject);
        activityLogService.log(currentUser(userDetails),
                "Admin updated teacher ID " + id);
        ra.addFlashAttribute("success", "Teacher updated.");
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes ra) {
        teacherService.deleteById(id);
        activityLogService.log(currentUser(userDetails),
                "Admin deleted teacher ID " + id);
        ra.addFlashAttribute("success", "Teacher deleted.");
        return "redirect:/admin/teachers";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STUDENTS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/students")
    public String students(@RequestParam(required = false) String search,
                           @RequestParam(required = false) String gradeLevel,
                           Model model) {
        List<Student> students;
        if (search != null && !search.isBlank()) {
            students = studentService.search(search);
        } else if (gradeLevel != null && !gradeLevel.isBlank()) {
            students = studentService.findByGradeLevel(gradeLevel);
        } else {
            students = studentService.findAll();
        }
        model.addAttribute("students", students);
        model.addAttribute("search", search);
        model.addAttribute("selectedGrade", gradeLevel);
        model.addAttribute("gradeLevels", studentService.getAllGradeLevels());
        return "admin/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String studentId,
                             @RequestParam(required = false) String gradeLevel,
                             @RequestParam(required = false) String section,
                             @RequestParam(required = false) String email,
                             @RequestParam String password,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes ra) {
        studentService.createStudent(firstName, lastName, studentId, gradeLevel, section, email, password);
        activityLogService.log(currentUser(userDetails),
                "Admin added student: " + firstName + " " + lastName);
        ra.addFlashAttribute("success", "Student added successfully.");
        return "redirect:/admin/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        studentService.findById(id).ifPresent(s -> model.addAttribute("student", s));
        return "admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        studentService.findById(id).ifPresent(s -> model.addAttribute("editStudent", s));
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("gradeLevels", studentService.getAllGradeLevels());
        return "admin/students";
    }

    @PostMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam(required = false) String gradeLevel,
                              @RequestParam(required = false) String section,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes ra) {
        studentService.update(id, firstName, lastName, gradeLevel, section);
        activityLogService.log(currentUser(userDetails),
                "Admin updated student ID " + id);
        ra.addFlashAttribute("success", "Student updated.");
        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes ra) {
        studentService.deleteById(id);
        activityLogService.log(currentUser(userDetails),
                "Admin deleted student ID " + id);
        ra.addFlashAttribute("success", "Student deleted.");
        return "redirect:/admin/students";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INCIDENTS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/incidents")
    public String incidents(@RequestParam(required = false) String status,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String type,
                            Model model) {
        List<Incident> incidents = incidentService.findAll();

        if (status != null && !status.isBlank()) {
            IncidentStatus s = IncidentStatus.valueOf(status);
            incidents = incidents.stream()
                    .filter(i -> i.getStatus() == s).collect(Collectors.toList());
        }
        if (type != null && !type.isBlank()) {
            IncidentType t = IncidentType.valueOf(type);
            incidents = incidents.stream()
                    .filter(i -> i.getIncidentType() == t).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            incidents = incidents.stream()
                    .filter(i -> (i.getStudent().getFirstName() + " " + i.getStudent().getLastName())
                            .toLowerCase().contains(kw)
                            || i.getIncidentType().name().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        model.addAttribute("incidents", incidents);
        model.addAttribute("statusFilter", status);
        model.addAttribute("selectedType", type != null && !type.isBlank() ? IncidentType.valueOf(type) : null);
        model.addAttribute("search", search);
        model.addAttribute("openCount",     incidentService.countByStatus(IncidentStatus.OPEN));
        model.addAttribute("reviewCount",   incidentService.countByStatus(IncidentStatus.UNDER_REVIEW));
        model.addAttribute("resolvedCount", incidentService.countByStatus(IncidentStatus.RESOLVED));
        return "admin/incidents";
    }

    @GetMapping("/incidents/{id}")
    public String viewIncident(@PathVariable Long id, Model model) {
        incidentService.findById(id).ifPresent(i -> model.addAttribute("incident", i));
        return "admin/incidents";
    }

    @PostMapping("/incidents/{id}/resolve")
    public String resolveIncident(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes ra) {
        incidentService.updateStatus(id, IncidentStatus.RESOLVED);
        activityLogService.log(currentUser(userDetails),
                "Admin resolved incident ID " + id);
        ra.addFlashAttribute("success", "Incident marked as resolved.");
        return "redirect:/admin/incidents";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SANCTIONS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/sanctions")
    public String sanctions(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String type,
                            Model model) {
        List<Sanction> sanctions = sanctionService.findAll();

        if (type != null && !type.isBlank()) {
            SanctionType st = SanctionType.valueOf(type);
            sanctions = sanctions.stream()
                    .filter(s -> s.getSanctionType() == st).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            sanctions = sanctions.stream()
                    .filter(s -> (s.getStudent().getFirstName() + " " + s.getStudent().getLastName())
                            .toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        model.addAttribute("sanctions", sanctions);
        model.addAttribute("search", search);
        model.addAttribute("selectedType", type != null && !type.isBlank() ? SanctionType.valueOf(type) : null);
        return "admin/sanctions";
    }

    @GetMapping("/sanctions/{id}")
    public String viewSanction(@PathVariable Long id, Model model) {
        sanctionService.findById(id).ifPresent(s -> model.addAttribute("sanction", s));
        return "admin/sanctions";
    }

    @PostMapping("/sanctions/delete/{id}")
    public String deleteSanction(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        sanctionService.deleteById(id);
        activityLogService.log(currentUser(userDetails),
                "Admin deleted sanction ID " + id);
        ra.addFlashAttribute("success", "Sanction removed.");
        return "redirect:/admin/sanctions";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FEEDBACK
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/feedback")
    public String feedback(@RequestParam(required = false) String role, Model model) {
        model.addAttribute("feedbackList",
                (role != null && !role.isBlank())
                        ? feedbackService.getFeedbackByRole(role)
                        : feedbackService.getAllFeedback());
        model.addAttribute("roleFilter", role);
        return "admin/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        feedbackService.deleteFeedback(id, currentUser(userDetails));
        ra.addFlashAttribute("success", "Feedback deleted.");
        return "redirect:/admin/feedback";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ANONYMOUS REPORTS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/anonymous-reports")
    public String anonymousReports(@RequestParam(required = false) String status, Model model) {
        List<AnonymousReport> reports = anonymousReportService.findAll();
        if ("PENDING".equals(status)) {
            reports = reports.stream().filter(r -> !r.isReviewed()).collect(Collectors.toList());
        } else if ("REVIEWED".equals(status)) {
            reports = reports.stream().filter(AnonymousReport::isReviewed).collect(Collectors.toList());
        }
        model.addAttribute("reports", reports);
        model.addAttribute("statusFilter", status);
        return "admin/anonymous-reports";
    }

    @PostMapping("/anonymous-reports/{id}/review")
    public String markReviewed(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes ra) {
        anonymousReportService.markReviewed(id);
        activityLogService.log(currentUser(userDetails),
                "Admin reviewed anonymous report ID " + id);
        ra.addFlashAttribute("success", "Report marked as reviewed.");
        return "redirect:/admin/anonymous-reports";
    }

    @PostMapping("/anonymous-reports/delete/{id}")
    public String deleteReport(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes ra) {
        anonymousReportService.deleteById(id);
        activityLogService.log(currentUser(userDetails),
                "Admin deleted anonymous report ID " + id);
        ra.addFlashAttribute("success", "Report deleted.");
        return "redirect:/admin/anonymous-reports";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACTIVITY HISTORY
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/activity-history")
    public String activityHistory(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String role,
                                  Model model) {
        model.addAttribute("logs",
                (search != null && !search.isBlank() && role != null && !role.isBlank())
                        ? activityLogService.searchLogsByKeywordAndRole(search, role)
                        : (search != null && !search.isBlank())
                                ? activityLogService.searchLogs(search)
                                : (role != null && !role.isBlank())
                                        ? activityLogService.getLogsByRole(role)
                                        : activityLogService.getAllLogs());
        model.addAttribute("search", search);
        model.addAttribute("roleFilter", role);
        return "admin/activity-history";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ANALYTICS
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/analytics")
    public String analytics(Model model) {
        long totalIncidents    = incidentService.countAll();
        long openIncidents     = incidentService.countByStatus(IncidentStatus.OPEN);
        long resolvedIncidents = incidentService.countByStatus(IncidentStatus.RESOLVED);
        long totalSanctions    = sanctionService.countAll();

        int resolutionRate = totalIncidents > 0
                ? (int) ((resolvedIncidents * 100) / totalIncidents) : 0;

        // ── Monthly incident counts for line chart ──
        List<String> monthLabels      = incidentService.getMonthLabels();
        List<Long>   incidentsPerMonth = incidentService.getCountsPerMonth();

        // ── By incident type ──
        List<String> incidentTypeLabels = incidentService.getTypeLabels();
        List<Long>   incidentTypeCounts = incidentService.getCountsPerType();

        // ── By sanction type ──
        List<String> sanctionTypeLabels = sanctionService.getTypeLabels();
        List<Long>   sanctionTypeCounts = sanctionService.getCountsPerType();

        // ── By grade level ──
        List<String> gradeLevelLabels  = incidentService.getGradeLevelLabels();
        List<Long>   gradeIncidentCounts = incidentService.getCountsPerGradeLevel();

        // ── Top 5 students by incident count ──
        List<Map<String, Object>> topStudents = incidentService.getTopStudentsByIncidentCount(5);

        model.addAttribute("totalIncidents",      totalIncidents);
        model.addAttribute("openIncidents",        openIncidents);
        model.addAttribute("resolvedIncidents",    resolvedIncidents);
        model.addAttribute("totalSanctions",       totalSanctions);
        model.addAttribute("resolutionRate",       resolutionRate);
        model.addAttribute("monthLabels",          monthLabels);
        model.addAttribute("incidentsPerMonth",    incidentsPerMonth);
        model.addAttribute("incidentTypeLabels",   incidentTypeLabels);
        model.addAttribute("incidentTypeCounts",   incidentTypeCounts);
        model.addAttribute("sanctionTypeLabels",   sanctionTypeLabels);
        model.addAttribute("sanctionTypeCounts",   sanctionTypeCounts);
        model.addAttribute("gradeLevelLabels",     gradeLevelLabels);
        model.addAttribute("gradeIncidentCounts",  gradeIncidentCounts);
        model.addAttribute("topStudents",          topStudents);

        return "admin/analytics";
    }
}
