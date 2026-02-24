package com.pallavi.resume_analyzer.service;

import com.pallavi.resume_analyzer.entity.ResumeAnalysis;
import com.pallavi.resume_analyzer.model.ResumeAnalysisResponse;
import com.pallavi.resume_analyzer.repository.ResumeAnalysisRepository;
import com.pallavi.resume_analyzer.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final AiService aiService;
    private final ResumeAnalysisRepository repository;

    public List<ResumeAnalysis> getHistory(){
        return repository.findAll();
    }

    public ResumeAnalysisResponse analyzeResume(MultipartFile file) {

        String resumeText = PdfUtil.extractText(file);

        String aiRaw = aiService.analyzeResumeText(resumeText);


        System.out.println("===== RAW AI RESPONSE =====");
        System.out.println(aiRaw);
        System.out.println("===========================");

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(aiRaw);

            String aiText = rootNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();


            aiText = aiText.replace("```json", "")
                    .replace("```", "")
                    .trim();


            int start = aiText.indexOf("{");
            int end = aiText.lastIndexOf("}");

            if(start == -1 || end == -1){
                throw new RuntimeException("JSON not found in AI response: " + aiText);
            }

            String jsonOnly = aiText.substring(start, end + 1);

            JsonNode parsed = mapper.readTree(jsonOnly);

            ResumeAnalysis entity = ResumeAnalysis.builder()
                    .fileName(file.getOriginalFilename())
                    .strengths(parsed.path("strengths").asText())
                    .missingSkills(parsed.path("missingSkills").asText())
                    .suggestions(parsed.path("suggestions").asText())
                    .jobMatchScore(parsed.path("jobMatchScore").asText())
                    .createdDate(LocalDateTime.now())
                    .build();

            repository.save(entity);

            return new ResumeAnalysisResponse(
                    entity.getStrengths(),
                    entity.getMissingSkills(),
                    List.of(entity.getSuggestions().split(",")),
                    entity.getJobMatchScore()
            );

        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error parsing AI response", e);
        }
    }

    public void deleteHistory(Long id){
        repository.deleteById(id);
    }
    public byte[] generatePdf(Long id){

        ResumeAnalysis r = repository.findById(id)
                .orElseThrow();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("Resume Analysis Report"));
            doc.add(new Paragraph("File: " + r.getFileName()));
            doc.add(new Paragraph("\nStrengths:\n" + r.getStrengths()));
            doc.add(new Paragraph("\nMissing Skills:\n" + r.getMissingSkills()));
            doc.add(new Paragraph("\nSuggestions:\n" + r.getSuggestions()));
            doc.add(new Paragraph("\nScore: " + r.getJobMatchScore()));

            doc.close();
            return out.toByteArray();

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public ResumeAnalysisResponse analyzeResumeWithJob(
            MultipartFile file, String jobDescription) {

        String resumeText = PdfUtil.extractText(file);

        String aiRaw = aiService.analyzeResumeWithJob(resumeText, jobDescription);

        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiRaw);

            String aiText = rootNode
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            aiText = aiText.replace("```json", "")
                    .replace("```", "")
                    .trim();

            int start = aiText.indexOf("{");
            int end = aiText.lastIndexOf("}");

            if(start == -1 || end == -1){
                throw new RuntimeException("JSON not found in AI response: " + aiText);
            }

            String jsonOnly = aiText.substring(start, end + 1);

            JsonNode parsed = mapper.readTree(jsonOnly);

            // ✅ extract suggestions list properly
            List<String> suggestions = new ArrayList<>();
            parsed.path("suggestions").forEach(n -> suggestions.add(n.asText()));

            // ✅ save to DB (INCLUDING job description)
            ResumeAnalysis entity = ResumeAnalysis.builder()
                    .fileName(file.getOriginalFilename())
                    .strengths(parsed.path("strengths").asText())
                    .missingSkills(parsed.path("missingSkills").asText())
                    .suggestions(String.join(", ", suggestions))
                    .jobMatchScore(parsed.path("jobMatchScore").asText())
                    .jobDescription(jobDescription)
                    .createdDate(LocalDateTime.now())
                    .build();

            repository.save(entity);

            // ✅ return response
            return new ResumeAnalysisResponse(
                    entity.getStrengths(),
                    entity.getMissingSkills(),
                    suggestions,
                    entity.getJobMatchScore()
            );

        } catch (Exception e){
            throw new RuntimeException("Error parsing AI response", e);
        }
    }

}






