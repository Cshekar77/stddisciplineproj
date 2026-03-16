package com.studentdiscipline.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "anonymous_reports")
public class AnonymousReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String reportedAgainst;

    private LocalDateTime submittedAt;

    private boolean reviewed;
}