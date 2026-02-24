package com.pallavi.resume_analyzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;

    @Column(length = 2000)
    private String strengths;

    @Column(length = 2000)
    private String missingSkills;

    @Column(length = 2000)
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private String jobMatchScore;

    private LocalDateTime createdDate;
}
