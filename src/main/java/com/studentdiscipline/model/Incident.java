package com.studentdiscipline.model;

import com.studentdiscipline.enums.IncidentType;
import com.studentdiscipline.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private String description;

    private LocalDate date;
}