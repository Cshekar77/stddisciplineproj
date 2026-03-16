package com.studentdiscipline.service;

import com.studentdiscipline.model.Sanction;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Incident;
import com.studentdiscipline.repository.SanctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SanctionService {

    private final SanctionRepository sanctionRepository;

    // Get all sanctions
    public List<Sanction> getAllSanctions() {
        return sanctionRepository.findAll();
    }

    // Get sanctions by student
    public List<Sanction> getSanctionsByStudent(Student student) {
        return sanctionRepository.findByStudent(student);
    }

    // Get sanctions by incident
    public List<Sanction> getSanctionsByIncident(Incident incident) {
        return sanctionRepository.findByIncident(incident);
    }

    // Add new sanction
    public Sanction saveSanction(Sanction sanction) {
        return sanctionRepository.save(sanction);
    }

    // Get sanction by id
    public Sanction getSanctionById(Long id) {
        return sanctionRepository.findById(id).orElse(null);
    }

    // Update sanction
    public Sanction updateSanction(Sanction sanction) {
        return sanctionRepository.save(sanction);
    }

    // Delete sanction
    public void deleteSanction(Long id) {
        sanctionRepository.deleteById(id);
    }
}