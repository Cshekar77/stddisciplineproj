package com.studentdiscipline.service;

import com.studentdiscipline.enums.SanctionType;
import com.studentdiscipline.model.Sanction;
import com.studentdiscipline.model.Student;
import com.studentdiscipline.model.Incident;
import com.studentdiscipline.repository.SanctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanctionService {

    private final SanctionRepository sanctionRepository;

    // Get all sanctions
    public List<Sanction> getAllSanctions() {
        return sanctionRepository.findAll();
    }

    // NEWLY ADDED: Count total sanctions (Required by AdminController)
    public long countAll() {
        return sanctionRepository.count();
    }

    // NEWLY ADDED: Get all sanctions (Alias for getAllSanctions - Required by AdminController)
    public List<Sanction> findAll() {
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

    // NEWLY ADDED: Find sanction by ID (Alias for getSanctionById - Required by AdminController)
    public Optional<Sanction> findById(Long id) {
        return sanctionRepository.findById(id);
    }

    // Update sanction
    public Sanction updateSanction(Sanction sanction) {
        return sanctionRepository.save(sanction);
    }

    // Delete sanction
    public void deleteSanction(Long id) {
        sanctionRepository.deleteById(id);
    }

    // NEWLY ADDED: Delete sanction by ID (Alias for deleteSanction - Required by AdminController)
    public void deleteById(Long id) {
        deleteSanction(id);
    }

    // ==================== NEWLY ADDED METHODS FOR ADMIN CONTROLLER ====================

    // NEWLY ADDED: Get sanction type labels for analytics (Required by AdminController)
    public List<String> getTypeLabels() {
        return Arrays.stream(SanctionType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // NEWLY ADDED: Get counts per sanction type for analytics (Required by AdminController)
    public List<Long> getCountsPerType() {
        return Arrays.stream(SanctionType.values())
                .map(type -> findAll().stream()
                        .filter(s -> s.getSanctionType() == type)
                        .count())
                .collect(Collectors.toList());
    }
}