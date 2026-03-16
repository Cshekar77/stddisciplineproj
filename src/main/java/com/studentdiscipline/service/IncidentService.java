package com.studentdiscipline.service;

import com.studentdiscipline.enums.IncidentStatus;
import com.studentdiscipline.enums.IncidentType;
import com.studentdiscipline.model.Incident;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Teacher;
import com.studentdiscipline.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // Get all incidents
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    // NEWLY ADDED: Count total incidents (Required by AdminController)
    public long countAll() {
        return incidentRepository.count();
    }

    // NEWLY ADDED: Get all incidents (Alias for getAllIncidents - Required by AdminController)
    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    // Get incidents by student
    public List<Incident> getIncidentsByStudent(Student student) {
        return incidentRepository.findByStudent(student);
    }

    // Get incidents by teacher
    public List<Incident> getIncidentsByTeacher(Teacher teacher) {
        return incidentRepository.findByTeacher(teacher);
    }

    // Add new incident
    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    // Get incident by id
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElse(null);
    }

    // NEWLY ADDED: Find incident by ID (Alias for getIncidentById - Required by AdminController)
    public Optional<Incident> findById(Long id) {
        return incidentRepository.findById(id);
    }

    // Update incident
    public Incident updateIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    // Delete incident
    public void deleteIncident(Long id) {
        incidentRepository.deleteById(id);
    }

    // ==================== NEWLY ADDED METHODS FOR ADMIN CONTROLLER ====================

    // NEWLY ADDED: Count incidents by status (Required by AdminController)
    public long countByStatus(IncidentStatus status) {
        return findAll().stream()
                .filter(i -> i.getStatus() == status)
                .count();
    }

    // NEWLY ADDED: Get recent incidents (Required by AdminController)
    public List<Incident> getRecent(int count) {
        return findAll().stream()
                .sorted(Comparator.comparing(Incident::getIncidentDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Update incident status (Required by AdminController)
    public Incident updateStatus(Long id, IncidentStatus status) {
        Optional<Incident> incidentOpt = findById(id);
        if (incidentOpt.isPresent()) {
            Incident incident = incidentOpt.get();
            incident.setStatus(status);
            return incidentRepository.save(incident);
        }
        throw new RuntimeException("Incident not found with ID: " + id);
    }

    // NEWLY ADDED: Get incident type labels for analytics (Required by AdminController)
    public List<String> getTypeLabels() {
        return Arrays.stream(IncidentType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Get counts per incident type for analytics (Required by AdminController)
    public List<Long> getCountsPerType() {
        return Arrays.stream(IncidentType.values())
                .map(type -> findAll().stream()
                        .filter(i -> i.getIncidentType() == type)
                        .count())
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Get month labels for analytics - last 12 months (Required by AdminController)
    public List<String> getMonthLabels() {
        List<String> labels = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            labels.add(month.format(DateTimeFormatter.ofPattern("MMM yyyy")));
        }
        return labels;
    }

    // NEWLY ADDED: Get incident counts per month for analytics (Required by AdminController)
    public List<Long> getCountsPerMonth() {
        List<Long> counts = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            long count = findAll().stream()
                    .filter(incident -> {
                        if (incident.getIncidentDate() == null) return false;
                        YearMonth incidentMonth = YearMonth.from(incident.getIncidentDate());
                        return incidentMonth.equals(month);
                    })
                    .count();
            counts.add(count);
        }
        return counts;
    }

    // NEWLY ADDED: Get grade level labels for analytics (Required by AdminController)
    public List<String> getGradeLevelLabels() {
        return findAll().stream()
                .map(i -> i.getStudent().getGrade())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Get counts per grade level for analytics (Required by AdminController)
    public List<Long> getCountsPerGradeLevel() {
        return getGradeLevelLabels().stream()
                .map(grade -> findAll().stream()
                        .filter(i -> grade.equals(i.getStudent().getGrade()))
                        .count())
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Get top N students by incident count (Required by AdminController)
    public List<Map<String, Object>> getTopStudentsByIncidentCount(int limit) {
        return findAll().stream()
                .collect(Collectors.groupingBy(
                        incident -> incident.getStudent(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("studentName", entry.getKey().getFirstName() + " " + 
                            entry.getKey().getLastName());
                    map.put("studentId", entry.getKey().getStudentId());
                    map.put("incidentCount", entry.getValue());
                    return map;
                })
                .sorted(Comparator.comparing((Map<String, Object> m) -> 
                        (Long) m.get("incidentCount")).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}