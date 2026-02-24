package com.pallavi.resume_analyzer.repository;

import com.pallavi.resume_analyzer.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
}
