package com.studentdiscipline.service;

import com.studentdiscipline.model.Incident;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Teacher;
import com.studentdiscipline.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // Get all incidents
    public List<Incident> getAllIncidents() {
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

    // Update incident
    public Incident updateIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    // Delete incident
    public void deleteIncident(Long id) {
        incidentRepository.deleteById(id);
    }
}