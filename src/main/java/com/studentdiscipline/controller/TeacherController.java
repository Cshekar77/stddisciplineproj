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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired private TeacherService teacherService;
    @Autowired private StudentService studentService;
    @Autowired private IncidentService incidentService;
    @Autowired private SanctionService sanctionService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private UserService userService;
    @Autowired private CaseService caseService;

    private User currentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Teacher currentTeacher(UserDetails userDetails) {
        User user = currentUser(userDetails);
        return teacherService.getTeacherByUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    // ✅ ADDED: Helper method to get student IDs for the current teacher
    private List<Long> getTeacherStudentIds(Teacher teacher) {
        return studentService.findByTeacherId(teacher.getId())
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            model.addAttribute("teacher", teacher);
            model.addAttribute("teacherName", teacher.getFullName());
            // ✅ FIXED: Use countByTeacherId instead of countAll
            model.addAttribute("totalStudents", studentService.countByTeacherId(teacher.getId()));
            model.addAttribute("myIncidents", incidentService.getIncidentsByTeacher(teacher).size());
            model.addAttribute("openIncidents", incidentService.getIncidentsByTeacher(teacher)
                    .stream().filter(i -> i.getStatus() == IncidentStatus.OPEN).count());
            model.addAttribute("recentIncidents", incidentService.getIncidentsByTeacher(teacher)
                    .stream().limit(5).collect(Collectors.toList()));
        } catch (Exception e) {
            model.addAttribute("teacherName", "Teacher");
            model.addAttribute("totalStudents", 0);
            model.addAttribute("myIncidents", 0);
            model.addAttribute("openIncidents", 0);
            model.addAttribute("recentIncidents", new ArrayList<>());
        }
        return "teacher/dashboard";
    }

    @GetMapping("/students")
    public String students(@RequestParam(required = false) String search, 
                          @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            // ✅ FIXED: Only show students assigned to this teacher
            List<Student> students = studentService.findByTeacherId(teacher.getId());
            
            if (search != null && !search.isBlank()) {
                String kw = search.toLowerCase();
                students = students.stream()
                        .filter(s -> s.getFirstName().toLowerCase().contains(kw) ||
                                    s.getLastName().toLowerCase().contains(kw) ||
                                    s.getStudentId().toLowerCase().contains(kw))
                        .collect(Collectors.toList());
            }
            
            model.addAttribute("students", students);
            model.addAttribute("search", search);
            model.addAttribute("disciplineStatuses", DisciplineStatus.values());
        } catch (Exception e) {
            model.addAttribute("students", new ArrayList<>());
        }
        return "teacher/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String firstName, @RequestParam String lastName,
                             @RequestParam String studentId,
                             @RequestParam(required = false) String grade,
                             @RequestParam(required = false) String section,
                             @RequestParam(required = false) String email,
                             @RequestParam String password,
                             @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            // ✅ FIXED: Use createStudentForTeacher to link student to teacher
            studentService.createStudentForTeacher(firstName, lastName, studentId,
                    grade != null ? grade : "N/A", section != null ? section : "N/A", email, password, teacher);
            activityLogService.log(currentUser(userDetails), "Teacher added student: " + firstName + " " + lastName);
            ra.addFlashAttribute("success", "Student added successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            studentService.deleteById(id);
            activityLogService.log(currentUser(userDetails), "Teacher deleted student ID " + id);
            ra.addFlashAttribute("success", "Student deleted.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/students";
    }

    @PostMapping("/students/{id}/discipline-status")
    public String updateDisciplineStatus(@PathVariable Long id,
                                         @RequestParam String disciplineStatus,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         RedirectAttributes ra) {
        try {
            studentService.setDisciplineStatus(id, DisciplineStatus.valueOf(disciplineStatus));
            activityLogService.log(currentUser(userDetails),
                    "Teacher set discipline status to " + disciplineStatus + " for student ID " + id);
            ra.addFlashAttribute("success", "Discipline status updated.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/students";
    }

    @GetMapping("/incidents")
    public String incidents(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            model.addAttribute("incidents", incidentService.getIncidentsByTeacher(teacher));
            // ✅ FIXED: Only show students assigned to this teacher in dropdown
            model.addAttribute("students", studentService.findByTeacherId(teacher.getId()));
            model.addAttribute("incidentTypes", IncidentType.values());
        } catch (Exception e) {
            model.addAttribute("incidents", new ArrayList<>());
            model.addAttribute("students", new ArrayList<>());
        }
        return "teacher/incidents";
    }

    @PostMapping("/incidents/add")
    public String addIncident(@RequestParam Long studentId, @RequestParam String incidentType,
                              @RequestParam String description,
                              @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Incident incident = new Incident();
            incident.setStudent(student);
            incident.setTeacher(teacher);
            incident.setIncidentType(IncidentType.valueOf(incidentType));
            incident.setStatus(IncidentStatus.OPEN);
            incident.setDescription(description);
            incident.setDate(LocalDate.now());
            incidentService.saveIncident(incident);
            activityLogService.log(currentUser(userDetails), "Teacher filed incident for student " + student.getFullName());
            ra.addFlashAttribute("success", "Incident filed successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/incidents";
    }

    @PostMapping("/incidents/delete/{id}")
    public String deleteIncident(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            incidentService.deleteIncident(id);
            activityLogService.log(currentUser(userDetails), "Teacher deleted incident ID " + id);
            ra.addFlashAttribute("success", "Incident deleted.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/incidents";
    }

    @GetMapping("/sanctions")
    public String sanctions(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            List<Long> studentIds = getTeacherStudentIds(teacher);
            
            // ✅ FIXED: Only show sanctions for students assigned to this teacher
            List<Sanction> filteredSanctions = sanctionService.findAll().stream()
                    .filter(s -> s.getStudent() != null && studentIds.contains(s.getStudent().getId()))
                    .collect(Collectors.toList());
            
            model.addAttribute("sanctions", filteredSanctions);
            // ✅ FIXED: Only show students assigned to this teacher in dropdown
            model.addAttribute("students", studentService.findByTeacherId(teacher.getId()));
            model.addAttribute("incidents", incidentService.getIncidentsByTeacher(teacher));
            model.addAttribute("sanctionTypes", SanctionType.values());
        } catch (Exception e) {
            model.addAttribute("sanctions", new ArrayList<>());
            model.addAttribute("students", new ArrayList<>());
            model.addAttribute("incidents", new ArrayList<>());
        }
        return "teacher/sanctions";
    }

    @PostMapping("/sanctions/add")
    public String addSanction(@RequestParam Long studentId,
                              @RequestParam(required = false) Long incidentId,
                              @RequestParam String sanctionType,
                              @RequestParam(required = false) String notes,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Sanction sanction = new Sanction();
            sanction.setStudent(student);
            sanction.setSanctionType(SanctionType.valueOf(sanctionType));
            sanction.setNotes(notes);
            if (startDate != null && !startDate.isBlank()) sanction.setStartDate(LocalDate.parse(startDate));
            if (endDate != null && !endDate.isBlank()) sanction.setEndDate(LocalDate.parse(endDate));
            if (incidentId != null) incidentService.findById(incidentId).ifPresent(sanction::setIncident);
            sanctionService.saveSanction(sanction);
            activityLogService.log(currentUser(userDetails), "Teacher applied sanction to student " + student.getFullName());
            ra.addFlashAttribute("success", "Sanction applied successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/sanctions";
    }

    @PostMapping("/sanctions/delete/{id}")
    public String deleteSanction(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            sanctionService.deleteById(id);
            activityLogService.log(currentUser(userDetails), "Teacher deleted sanction ID " + id);
            ra.addFlashAttribute("success", "Sanction deleted.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/sanctions";
    }

    @GetMapping("/feedback")
    public String feedback(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = currentUser(userDetails);
            model.addAttribute("feedbackList", feedbackService.getFeedbackByUser(user.getId()));
        } catch (Exception e) { model.addAttribute("feedbackList", new ArrayList<>()); }
        return "teacher/feedback";
    }

    @PostMapping("/feedback/submit")
    public String submitFeedback(@RequestParam String message,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            User user = currentUser(userDetails);
            feedbackService.submitFeedback(message, "GENERAL", user);
            activityLogService.log(user, "Teacher submitted feedback.");
            ra.addFlashAttribute("success", "Feedback submitted successfully.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        try {
            feedbackService.deleteFeedback(id, currentUser(userDetails));
            ra.addFlashAttribute("success", "Feedback deleted.");
        } catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/teacher/feedback";
    }

    @GetMapping("/activity")
    public String activity(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = currentUser(userDetails);
            model.addAttribute("logs", activityLogService.getLogsByUser(user.getId()));
            model.addAttribute("teacherName", user.getFirstName() + " " + user.getLastName());
        } catch (Exception e) { model.addAttribute("logs", new ArrayList<>()); }
        return "teacher/activity-history";
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "teacher/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/teacher/change-password";
        }
        if (newPassword.length() < 6) {
            ra.addFlashAttribute("error", "New password must be at least 6 characters.");
            return "redirect:/teacher/change-password";
        }
        boolean success = userService.changePassword(userDetails.getUsername(), currentPassword, newPassword);
        if (!success) {
            ra.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/teacher/change-password";
        }
        activityLogService.log(currentUser(userDetails), "Teacher changed their own password.");
        ra.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/teacher/change-password";
    }

    // ── CASE MANAGEMENT ───────────────────────────────────────────────────────

    @GetMapping("/case-search")
    public String caseSearch(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String status, 
                             @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            List<Long> studentIds = getTeacherStudentIds(teacher);
            
            // ✅ FIXED: Only show cases for students assigned to this teacher
            List<Case> cases = caseService.getAllCases().stream()
                    .filter(c -> c.getStudent() != null && studentIds.contains(c.getStudent().getId()))
                    .collect(Collectors.toList());
            
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
        } catch (Exception e) {
            model.addAttribute("cases", new ArrayList<>());
        }
        return "teacher/case-search";
    }

    @GetMapping("/case-profile")
    public String caseProfile(@RequestParam Long id, Model model) {
        Case c = caseService.getCaseById(id);
        model.addAttribute("case", c);
        model.addAttribute("notes", caseService.getCaseNotes(id));
        model.addAttribute("caseStatuses", CaseStatus.values());
        return "teacher/case-profile";
    }

    @GetMapping("/case-timeline")
    public String caseTimeline(@RequestParam Long id, Model model) {
        Case c = caseService.getCaseById(id);
        model.addAttribute("case", c);
        model.addAttribute("notes", caseService.getCaseNotes(id));
        return "teacher/case-timeline";
    }

    @GetMapping("/case-reports")
    public String caseReports(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Teacher teacher = currentTeacher(userDetails);
            List<Long> studentIds = getTeacherStudentIds(teacher);
            
            // ✅ FIXED: Only show case reports for students assigned to this teacher
            List<Case> teacherCases = caseService.getAllCases().stream()
                    .filter(c -> c.getStudent() != null && studentIds.contains(c.getStudent().getId()))
                    .collect(Collectors.toList());
            
            model.addAttribute("totalCases", teacherCases.size());
            model.addAttribute("openCases", teacherCases.stream().filter(c -> c.getStatus() == CaseStatus.OPEN).count());
            model.addAttribute("resolvedCases", teacherCases.stream().filter(c -> c.getStatus() == CaseStatus.RESOLVED).count());
            model.addAttribute("closedCases", teacherCases.stream().filter(c -> c.getStatus() == CaseStatus.CLOSED).count());
            model.addAttribute("underReviewCases", teacherCases.stream().filter(c -> c.getStatus() == CaseStatus.UNDER_REVIEW).count());
            model.addAttribute("appealPendingCases", teacherCases.stream().filter(c -> c.getStatus() == CaseStatus.APPEAL_PENDING).count());
            model.addAttribute("allCases", teacherCases);
        } catch (Exception e) {
            model.addAttribute("totalCases", 0);
            model.addAttribute("allCases", new ArrayList<>());
        }
        return "teacher/case-reports";
    }
}