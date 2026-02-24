package com.pallavi.resume_analyzer.controller;

import com.pallavi.resume_analyzer.entity.ResumeAnalysis;
import com.pallavi.resume_analyzer.model.ResumeAnalysisResponse;
import com.pallavi.resume_analyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService service;

    @PostMapping("/analyze")
    public ResumeAnalysisResponse analyze(@RequestParam("file")MultipartFile file){
        return service.analyzeResume(file);
    }

    @GetMapping("/history")
    public List<ResumeAnalysis> history(){
        return service.getHistory();
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]>pdf(@PathVariable Long id){
        byte[] data = service.generatePdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition","attachment; filename=analysis.pdf")
                .body(data);
    }

    @DeleteMapping("/history/{id}")
    public void delete(@PathVariable Long id){
        service.deleteHistory(id);
    }

    @PostMapping("/analyze-job")
    public ResumeAnalysisResponse analyzeWithJob(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription){
        return service.analyzeResumeWithJob(file,jobDescription);
    }

}
