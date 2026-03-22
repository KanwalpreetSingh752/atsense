package com.atsense.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient webClient = WebClient.create();

    public String analyzeResume(String resumeText, String jobProfile) {
        String prompt = buildAnalysisPrompt(resumeText, jobProfile);
        return callGemini(prompt);
    }

    public String generateCoverLetter(String resumeText, String jobProfile) {
        String prompt = "Based on this resume:\n" + resumeText +
                "\nAnd this job profile:\n" + jobProfile +
                "\nWrite a professional cover letter. Respond ONLY in JSON: {\"coverLetter\": \"...\"}" ;
        return callGemini(prompt);
    }

    public String predictInterviewQuestions(String resumeText, String jobProfile) {
        String prompt = "Based on this resume and job profile, predict 10 likely interview questions." +
                "\nResume: " + resumeText + "\nJob: " + jobProfile +
                "\nRespond ONLY in JSON: {\"questions\": [...]}";
        return callGemini(prompt);
    }

    public String suggestCareerPaths(String resumeText) {
        String prompt = "Based on this resume, suggest 5 suitable career paths with reasons." +
                "\nResume: " + resumeText +
                "\nRespond ONLY in JSON: {\"paths\": [{\"title\": \"\", \"reason\": \"\"}]}";
        return callGemini(prompt);
    }

    public String rewriteBulletPoints(String resumeText) {
        String prompt = "Rewrite weak bullet points in this resume to be stronger and more impactful." +
                "\nResume: " + resumeText +
                "\nRespond ONLY in JSON: {\"rewrites\": [{\"original\": \"\", \"improved\": \"\"}]}";
        return callGemini(prompt);
    }

    private String buildAnalysisPrompt(String resume, String job) {
        return "Analyze this resume against the job profile and respond ONLY in JSON format:\n" +
                "{\n" +
                "  \"fitScore\": 0-100,\n" +
                "  \"keywordScore\": 0-100,\n" +
                "  \"formattingScore\": 0-100,\n" +
                "  \"sectionScore\": 0-100,\n" +
                "  \"readabilityScore\": 0-100,\n" +
                "  \"matchedSkills\": [],\n" +
                "  \"missingSkills\": [],\n" +
                "  \"suggestions\": [],\n" +
                "  \"verdict\": \"string\"\n" +
                "}\n" +
                "Resume: " + resume + "\n" +
                "Job Profile: " + job;
    }

    private String callGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );
        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

