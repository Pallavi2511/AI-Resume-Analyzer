package com.pallavi.resume_analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ResumeAnalysisResponse {

    private String strengths;
    private String missingSkills;
    private List<String> suggestions;
    private String jobMatchScore;

}
