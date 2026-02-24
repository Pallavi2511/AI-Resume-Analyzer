package com.pallavi.resume_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public String analyzeResumeText(String resumeText){

        String prompt = """
        Analyze this resume and respond ONLY in valid JSON.

        Format:
        {
        "strengths": "...",
        "missingSkills": "...",
        "suggestions": "...",
        "jobMatchScore": "..."
        }

        Resume:
        """ + resumeText;


        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;



        Map<String, Object> requestBody =
                Map.of(
                        "contents", new Object[]{
                                Map.of(
                                        "role","user",
                                        "parts", new Object[]{
                                                Map.of("text", prompt)
                                        }
                                )
                        }
                );

        String response = webClient.post()
                .uri(url)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
    public String analyzeResumeWithJob(String resumeText, String jobDescription){

        String prompt = """
Analyze this resume against the given job description.

Return ONLY valid JSON:
{
"strengths":"bullet points",
"missingSkills":"comma separated list",
"suggestions":["point1","point2","point3"],
"jobMatchScore":"number"
}

RESUME:
""" + resumeText + """

JOB DESCRIPTION:
""" + jobDescription;

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        Map<String,Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
