package com.pallavi.resume_analyzer.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResumeJobRequest {
    private MultipartFile file;
    private String jobDescription;

}
