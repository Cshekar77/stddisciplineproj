package com.studentdiscipline.controller;

import com.studentdiscipline.enums.CaseStatus;
import com.studentdiscipline.enums.DisciplineStatus;
import com.studentdiscipline.enums.IncidentStatus;
import com.studentdiscipline.enums.IncidentType;
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

import java.time.LocalDateTime;
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
    @Autowired private CaseService caseService;

    private User currentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }

    // ── DASHBOARD ─────────────────────────────────────────────────────────────

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User admin = currentUser(userDetails);
            model.addAttribute("adminName", admin.getFirstName() + " " + admin.getLastName());
            model.addAttribute("totalStudents",     studentService.countAll());
            model.addAttribute("totalTeachers",     teacherService.countAll());
            model.addAttribute("openIncidents",     incidentService.countByStatus(IncidentStatus.OPEN));
            model.addAttribute("resolvedIncidents", incidentService.countByStatus(IncidentStatus.RESOLVED));
            model.addAttribute("recentIncidents",   incidentService.getRecent(5));
            model.addAttribute("recentActivity",    activityLogService.getRecentLogs());
        } catch (Exception e) {
            model.addAttribute("adminName", "Admin");
            model.addAttribute("totalStudents", 0);
            model.addAttribute("totalTeachers", 0);
            model.addAttribute("openIncidents", 0);
            model.addAttribute("resolvedIncidents", 0);
            model.addAttribute("recentIncidents", new ArrayList<>());
            model.addAttribute("recentActivity", new ArrayList<>());
        }
        return "admin/dashboard";
    }

    // ── CREDENTIALS ───────────────────────────────────────────────────────────

    @GetMapping("/credentials")
    public String credentials(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        model.addAttribute("students", studentService.findAll());
        return "admin/credentials";
    }

    @PostMapping("/credentials/teacher")
    public String createTeacherCredentials(@RequestParam Long teacherId,
                                           @RequestParam String username,
                                           @RequestParam String password,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes ra) {
        try {
            teacherService.findById(teacherId).ifPresent(teacher -> {
                if (teacher.getUser() != null)
                    throw new RuntimeException("Teacher already has an account. Use Edit or Reset instead.");
                User user = userService.createUser(username, password, com.studentdiscipline.enums.Role.TEACHER);
                user.setFirstName(teacher.getFirstName());
                user.setLastName(teacher.getLastName());
                teacher.setUser(user);
                teacherService.saveTeacher(teacher);
                activityLogService.log(currentUser(userDetails), "Admin created credentials for teacher: " + teacher.getFullName());
            });
            ra.addFlashAttribute("success", "Teacher credentials created successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    @PostMapping("/credentials/teacher/edit")
    public String editTeacherCredentials(@RequestParam Long teacherId, @RequestParam String username,
                                         @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            teacherService.findById(teacherId).ifPresent(teacher -> {
                if (teacher.getUser() == null) throw new RuntimeException("Teacher has no account yet.");
                userService.updateUsername(teacher.getUser().getId(), username);
                activityLogService.log(currentUser(userDetails), "Admin updated username for teacher: " + teacher.getFullName());
            });
            ra.addFlashAttribute("success", "Teacher username updated.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    @PostMapping("/credentials/teacher/reset")
    public String resetTeacherPassword(@RequestParam Long teacherId, @RequestParam String password,
                                       @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            teacherService.findById(teacherId).ifPresent(teacher -> {
                if (teacher.getUser() == null) throw new RuntimeException("Teacher has no account yet.");
                userService.updatePassword(teacher.getUser().getId(), password);
                activityLogService.log(currentUser(userDetails), "Admin reset password for teacher: " + teacher.getFullName());
            });
            ra.addFlashAttribute("success", "Teacher password reset successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    @PostMapping("/credentials/student")
    public String createStudentCredentials(@RequestParam Long studentId, @RequestParam String username,
                                           @RequestParam String password,
                                           @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.findById(studentId).ifPresent(student -> {
                if (student.getUser() != null)
                    throw new RuntimeException("Student already has an account. Use Edit or Reset instead.");
                User user = userService.createUser(username, password, com.studentdiscipline.enums.Role.STUDENT);
                user.setFirstName(student.getFirstName());
                user.setLastName(student.getLastName());
                student.setUser(user);
                studentService.saveStudent(student);
                activityLogService.log(currentUser(userDetails), "Admin created credentials for student: " + student.getFullName());
            });
            ra.addFlashAttribute("success", "Student credentials created successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    @PostMapping("/credentials/student/edit")
    public String editStudentCredentials(@RequestParam Long studentId, @RequestParam String username,
                                         @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.findById(studentId).ifPresent(student -> {
                if (student.getUser() == null) throw new RuntimeException("Student has no account yet.");
                userService.updateUsername(student.getUser().getId(), username);
                activityLogService.log(currentUser(userDetails), "Admin updated username for student: " + student.getFullName());
            });
            ra.addFlashAttribute("success", "Student username updated.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    @PostMapping("/credentials/student/reset")
    public String resetStudentPassword(@RequestParam Long studentId, @RequestParam String password,
                                       @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.findById(studentId).ifPresent(student -> {
                if (student.getUser() == null) throw new RuntimeException("Student has no account yet.");
                userService.updatePassword(student.getUser().getId(), password);
                activityLogService.log(currentUser(userDetails), "Admin reset password for student: " + student.getFullName());
            });
            ra.addFlashAttribute("success", "Student password reset successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/credentials";
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/admin/change-password";
        }
        if (newPassword.length() < 6) {
            ra.addFlashAttribute("error", "New password must be at least 6 characters.");
            return "redirect:/admin/change-password";
        }
        boolean success = userService.changePassword(userDetails.getUsername(), currentPassword, newPassword);
        if (!success) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/admin/change-password";
        }
        activityLogService.log(currentUser(userDetails), "Admin changed their own password.");
        ra.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/admin/change-password";
    }

    // ── TEACHERS ──────────────────────────────────────────────────────────────

    @GetMapping("/teachers")
    public String teachers(@RequestParam(required = false) String search, Model model) {
        List<Teacher> teachers = (search != null && !search.isBlank())
                ? teacherService.search(search) : teacherService.findAll();
        model.addAttribute("teachers", teachers);
        model.addAttribute("search", search);
        return "admin/teachers";
    }

    @PostMapping("/teachers/add")
    public String addTeacher(@RequestParam String firstName, @RequestParam String lastName,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) String email,
                             @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            teacherService.createTeacherNoCredentials(firstName, lastName,
                    department != null && !department.isBlank() ? department : "General", email);
            activityLogService.log(currentUser(userDetails), "Admin added teacher: " + firstName + " " + lastName);
            ra.addFlashAttribute("success", "Teacher added. Set login credentials via Credentials page.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/edit/{id}")
    public String editTeacher(@PathVariable Long id, @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam(required = false) String department,
                              @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            teacherService.update(id, firstName, lastName, department);
            activityLogService.log(currentUser(userDetails), "Admin updated teacher ID " + id);
            ra.addFlashAttribute("success", "Teacher updated.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            teacherService.deleteById(id);
            activityLogService.log(currentUser(userDetails), "Admin deleted teacher ID " + id);
            ra.addFlashAttribute("success", "Teacher deleted.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/teachers";
    }

    // ── STUDENTS ──────────────────────────────────────────────────────────────

    @GetMapping("/students")
    public String students(@RequestParam(required = false) String search,
                           @RequestParam(required = false) String grade, Model model) {
        List<Student> students;
        if (search != null && !search.isBlank()) students = studentService.search(search);
        else if (grade != null && !grade.isBlank()) students = studentService.findByGradeLevel(grade);
        else students = studentService.findAll();
        model.addAttribute("students", students);
        model.addAttribute("search", search);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("grades", studentService.getAllGradeLevels());
        model.addAttribute("disciplineStatuses", DisciplineStatus.values());
        return "admin/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String firstName, @RequestParam String lastName,
                             @RequestParam String studentId,
                             @RequestParam(required = false) String grade,
                             @RequestParam(required = false) String section,
                             @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.createStudentNoCredentials(firstName, lastName, studentId,
                    grade != null ? grade : "N/A", section != null ? section : "N/A");
            activityLogService.log(currentUser(userDetails), "Admin added student: " + firstName + " " + lastName);
            ra.addFlashAttribute("success", "Student added. Set login credentials via Credentials page.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id, @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam(required = false) String grade,
                              @RequestParam(required = false) String section,
                              @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.update(id, firstName, lastName, grade, section);
            studentService.updateDisciplineStatus(id);
            activityLogService.log(currentUser(userDetails), "Admin updated student ID " + id);
            ra.addFlashAttribute("success", "Student updated.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/discipline-status")
    public String updateDisciplineStatus(@PathVariable Long id,
                                         @RequestParam String disciplineStatus,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         RedirectAttributes ra) {
        try {
            studentService.setDisciplineStatus(id, DisciplineStatus.valueOf(disciplineStatus));
            activityLogService.log(currentUser(userDetails),
                    "Admin set discipline status to " + disciplineStatus + " for student ID " + id);
            ra.addFlashAttribute("success", "Discipline status updated.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.deleteById(id);
            activityLogService.log(currentUser(userDetails), "Admin deleted student ID " + id);
            ra.addFlashAttribute("success", "Student deleted.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/students";
    }

    // ── INCIDENTS ─────────────────────────────────────────────────────────────

    @GetMapping("/incidents")
    public String incidents(@RequestParam(required = false) String status,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String type, Model model) {
        List<Incident> incidents = incidentService.findAll();
        if (status != null && !status.isBlank()) {
            IncidentStatus s = IncidentStatus.valueOf(status);
            incidents = incidents.stream().filter(i -> i.getStatus() == s).collect(Collectors.toList());
        }
        if (type != null && !type.isBlank()) {
            IncidentType t = IncidentType.valueOf(type);
            incidents = incidents.stream().filter(i -> i.getIncidentType() == t).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            incidents = incidents.stream()
                    .filter(i -> i.getStudent() != null &&
                            (i.getStudent().getFirstName() + " " + i.getStudent().getLastName()).toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }
        model.addAttribute("incidents", incidents);
        model.addAttribute("statusFilter", status);
        model.addAttribute("search", search);
        model.addAttribute("openCount",     incidentService.countByStatus(IncidentStatus.OPEN));
        model.addAttribute("reviewCount",   incidentService.countByStatus(IncidentStatus.UNDER_REVIEW));
        model.addAttribute("resolvedCount", incidentService.countByStatus(IncidentStatus.RESOLVED));
        return "admin/incidents";
    }

    @PostMapping("/incidents/{id}/resolve")
    public String resolveIncident(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            incidentService.updateStatus(id, IncidentStatus.RESOLVED);
            activityLogService.log(currentUser(userDetails), "Admin resolved incident ID " + id);
            ra.addFlashAttribute("success", "Incident marked as resolved.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/incidents";
    }

    // ── SANCTIONS ─────────────────────────────────────────────────────────────

    @GetMapping("/sanctions")
    public String sanctions(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String type, Model model) {
        List<Sanction> sanctions = sanctionService.findAll();
        if (type != null && !type.isBlank()) {
            SanctionType st = SanctionType.valueOf(type);
            sanctions = sanctions.stream().filter(s -> s.getSanctionType() == st).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            sanctions = sanctions.stream()
                    .filter(s -> s.getStudent() != null &&
                            (s.getStudent().getFirstName() + " " + s.getStudent().getLastName()).toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }
        model.addAttribute("sanctions", sanctions);
        model.addAttribute("search", search);
        return "admin/sanctions";
    }

    @PostMapping("/sanctions/delete/{id}")
    public String deleteSanction(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            sanctionService.deleteById(id);
            activityLogService.log(currentUser(userDetails), "Admin deleted sanction ID " + id);
            ra.addFlashAttribute("success", "Sanction removed.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/sanctions";
    }

    // ── FEEDBACK ──────────────────────────────────────────────────────────────

    @GetMapping("/feedback")
    public String feedback(@RequestParam(required = false) String role, Model model) {
        model.addAttribute("feedbackList", (role != null && !role.isBlank())
                ? feedbackService.getFeedbackByRole(role) : feedbackService.getAllFeedback());
        model.addAttribute("roleFilter", role);
        return "admin/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            feedbackService.deleteFeedback(id, currentUser(userDetails));
            ra.addFlashAttribute("success", "Feedback deleted.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/feedback";
    }

    // ── ANONYMOUS REPORTS ─────────────────────────────────────────────────────

    @GetMapping("/anonymous-reports")
    public String anonymousReports(@RequestParam(required = false) String status, Model model) {
        List<AnonymousReport> reports = anonymousReportService.getAllReports();
        if ("PENDING".equals(status)) reports = reports.stream().filter(r -> !r.isReviewed()).collect(Collectors.toList());
        else if ("REVIEWED".equals(status)) reports = reports.stream().filter(AnonymousReport::isReviewed).collect(Collectors.toList());
        model.addAttribute("reports", reports);
        model.addAttribute("statusFilter", status);
        return "admin/anonymous-reports";
    }

    @PostMapping("/anonymous-reports/{id}/review")
    public String markReviewed(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            anonymousReportService.markAsReviewed(id);
            activityLogService.log(currentUser(userDetails), "Admin reviewed anonymous report ID " + id);
            ra.addFlashAttribute("success", "Report marked as reviewed.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/anonymous-reports";
    }

    @PostMapping("/anonymous-reports/delete/{id}")
    public String deleteReport(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            anonymousReportService.deleteReport(id);
            activityLogService.log(currentUser(userDetails), "Admin deleted anonymous report ID " + id);
            ra.addFlashAttribute("success", "Report deleted.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/anonymous-reports";
    }

    // ── ACTIVITY HISTORY ──────────────────────────────────────────────────────

    @GetMapping("/activity-history")
    public String activityHistory(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String role, Model model) {
        model.addAttribute("logs",
                (search != null && !search.isBlank() && role != null && !role.isBlank())
                        ? activityLogService.searchLogsByKeywordAndRole(search, role)
                        : (search != null && !search.isBlank()) ? activityLogService.searchLogs(search)
                        : (role != null && !role.isBlank()) ? activityLogService.getLogsByRole(role)
                        : activityLogService.getAllLogs());
        model.addAttribute("search", search);
        model.addAttribute("roleFilter", role);
        return "admin/activity-history";
    }

    // ── ANALYTICS ─────────────────────────────────────────────────────────────

    @GetMapping("/analytics")
    public String analytics(Model model) {
        try {
            long totalIncidents    = incidentService.countAll();
            long openIncidents     = incidentService.countByStatus(IncidentStatus.OPEN);
            long resolvedIncidents = incidentService.countByStatus(IncidentStatus.RESOLVED);
            long totalSanctions    = sanctionService.countAll();
            int resolutionRate = totalIncidents > 0 ? (int)((resolvedIncidents * 100) / totalIncidents) : 0;
            model.addAttribute("totalIncidents", totalIncidents);
            model.addAttribute("openIncidents", openIncidents);
            model.addAttribute("resolvedIncidents", resolvedIncidents);
            model.addAttribute("totalSanctions", totalSanctions);
            model.addAttribute("resolutionRate", resolutionRate);
            model.addAttribute("monthLabels", incidentService.getMonthLabels());
            model.addAttribute("incidentsPerMonth", incidentService.getCountsPerMonth());
            model.addAttribute("incidentTypeLabels", incidentService.getTypeLabels());
            model.addAttribute("incidentTypeCounts", incidentService.getCountsPerType());
            model.addAttribute("sanctionTypeLabels", sanctionService.getTypeLabels());
            model.addAttribute("sanctionTypeCounts", sanctionService.getCountsPerType());
            model.addAttribute("gradeLevelLabels", incidentService.getGradeLevelLabels());
            model.addAttribute("gradeIncidentCounts", incidentService.getCountsPerGradeLevel());
            model.addAttribute("topStudents", incidentService.getTopStudentsByIncidentCount(5));
        } catch (Exception e) { model.addAttribute("error", "Could not load analytics: " + e.getMessage()); }
        return "admin/analytics";
    }

    // ── CASE MANAGEMENT ───────────────────────────────────────────────────────

    @GetMapping("/case-search")
    public String caseSearch(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String status, Model model) {
        List<Case> cases = caseService.getAllCases();
        if (status != null && !status.isBlank()) {
            CaseStatus cs = CaseStatus.valueOf(status);
            cases = cases.stream().filter(c -> c.getStatus() == cs).collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            cases = cases.stream()
                    .filter(c -> c.getStudent() != null &&
                            (c.getStudent().getFirstName() + " " + c.getStudent().getLastName()).toLowerCase().contains(kw)
                            || c.getCaseNumber().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }
        model.addAttribute("cases", cases);
        model.addAttribute("search", search);
        model.addAttribute("statusFilter", status);
        model.addAttribute("caseStatuses", CaseStatus.values());
        return "admin/case-search";
    }

    @GetMapping("/case-profile")
    public String caseProfile(@RequestParam Long id, Model model) {
        Case c = caseService.getCaseById(id);
        model.addAttribute("case", c);
        model.addAttribute("notes", caseService.getCaseNotes(id));
        model.addAttribute("caseStatuses", CaseStatus.values());
        return "admin/case-profile";
    }

    @GetMapping("/case-timeline")
    public String caseTimeline(@RequestParam Long id, Model model) {
        Case c = caseService.getCaseById(id);
        model.addAttribute("case", c);
        model.addAttribute("notes", caseService.getCaseNotes(id));
        return "admin/case-timeline";
    }

    @GetMapping("/case-reports")
    public String caseReports(Model model) {
        model.addAttribute("totalCases", caseService.getAllCases().size());
        model.addAttribute("openCases", caseService.getCasesByStatus(CaseStatus.OPEN).size());
        model.addAttribute("resolvedCases", caseService.getCasesByStatus(CaseStatus.RESOLVED).size());
        model.addAttribute("closedCases", caseService.getCasesByStatus(CaseStatus.CLOSED).size());
        model.addAttribute("underReviewCases", caseService.getCasesByStatus(CaseStatus.UNDER_REVIEW).size());
        model.addAttribute("appealPendingCases", caseService.getCasesByStatus(CaseStatus.APPEAL_PENDING).size());
        model.addAttribute("allCases", caseService.getAllCases());
        return "admin/case-reports";
    }

    // ── ADD NEW CASE ──────────────────────────────────────────────────────────

    @GetMapping("/case-add")
    public String showAddCaseForm(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "admin/case-add";
    }

    @PostMapping("/case-add")
    public String addCase(@RequestParam Long studentId,
                          @RequestParam String caseNumber,
                          @RequestParam String title,
                          @RequestParam(required = false) String status,
                          @RequestParam(required = false) String description,
                          RedirectAttributes ra) {
        try {
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            Case newCase = new Case();
            newCase.setStudent(student);
            newCase.setCaseNumber(caseNumber);
            newCase.setTitle(title);
            newCase.setDescription(description);
            newCase.setStatus(CaseStatus.valueOf(status != null ? status : "OPEN"));
            newCase.setCreatedDate(LocalDateTime.now());
            newCase.setUpdatedDate(LocalDateTime.now());
            
            caseService.createCase(newCase);
            ra.addFlashAttribute("success", "Case created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/case-add";
    }
}