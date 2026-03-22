package com.atsense.backend.service;

import com.atsense.backend.dto.JobProfileRequest;
import com.atsense.backend.model.AnalysisResult;
import com.atsense.backend.model.JobProfile;
import com.atsense.backend.model.Resume;
import com.atsense.backend.model.User;
import com.atsense.backend.repository.AnalysisResultRepository;
import com.atsense.backend.repository.JobProfileRepository;
import com.atsense.backend.repository.ResumeRepository;
import com.atsense.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {

    @Autowired private GeminiService geminiService;
    @Autowired private AnalysisResultRepository analysisResultRepository;
    @Autowired private JobProfileRepository jobProfileRepository;
    @Autowired private ResumeRepository resumeRepository;
    @Autowired private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisResult analyze(Resume resume, JobProfileRequest jobProfileRequest, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Save job profile
            JobProfile jobProfile = new JobProfile();
            jobProfile.setUser(user);
            jobProfile.setTitle(jobProfileRequest.getTitle());
            jobProfile.setSkills(jobProfileRequest.getSkills());
            jobProfile.setExperienceYears(jobProfileRequest.getExperienceYears());
            jobProfile.setEducation(jobProfileRequest.getEducation());
            jobProfile.setIndustry(jobProfileRequest.getIndustry());
            jobProfile = jobProfileRepository.save(jobProfile);

            // Build job profile string for Gemini
            String jobProfileStr = "Title: " + jobProfile.getTitle()
                    + "\nSkills: " + jobProfile.getSkills()
                    + "\nExperience: " + jobProfile.getExperienceYears() + " years"
                    + "\nEducation: " + jobProfile.getEducation()
                    + "\nIndustry: " + jobProfile.getIndustry();

            // Call Gemini
            String geminiResponse = geminiService.analyzeResume(resume.getParsedText(), jobProfileStr);

            // Parse Gemini response
            JsonNode root = objectMapper.readTree(extractJson(geminiResponse));

            // Build and save result
            AnalysisResult result = new AnalysisResult();
            result.setResume(resume);
            result.setJobProfile(jobProfile);
            result.setFitScore(root.path("fitScore").asInt());
            result.setKeywordScore(root.path("keywordScore").asInt());
            result.setFormattingScore(root.path("formattingScore").asInt());
            result.setSectionScore(root.path("sectionScore").asInt());
            result.setReadabilityScore(root.path("readabilityScore").asInt());

            List<String> suggestions = new ArrayList<>();
            root.path("suggestions").forEach(s -> suggestions.add(s.asText()));
            result.setSuggestions(suggestions);

            return analysisResultRepository.save(result);

        } catch (Exception e) {
            throw new RuntimeException("Analysis failed: " + e.getMessage());
        }
    }

    public List<AnalysisResult> getHistory(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return analysisResultRepository.findByResumeUserIdOrderByCreatedAtDesc(user.getId());
    }

    public String generateCoverLetter(Long resumeId, Long jobProfileId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        JobProfile jobProfile = jobProfileRepository.findById(jobProfileId)
                .orElseThrow(() -> new RuntimeException("Job profile not found"));
        String jobStr = "Title: " + jobProfile.getTitle() + "\nSkills: " + jobProfile.getSkills();
        return geminiService.generateCoverLetter(resume.getParsedText(), jobStr);
    }

    public String predictInterviewQuestions(Long resumeId, Long jobProfileId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        JobProfile jobProfile = jobProfileRepository.findById(jobProfileId)
                .orElseThrow(() -> new RuntimeException("Job profile not found"));
        String jobStr = "Title: " + jobProfile.getTitle() + "\nSkills: " + jobProfile.getSkills();
        return geminiService.predictInterviewQuestions(resume.getParsedText(), jobStr);
    }

    public String suggestCareerPaths(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        return geminiService.suggestCareerPaths(resume.getParsedText());
    }

    // Extracts JSON from Gemini response which wraps it in markdown code blocks
    private String extractJson(String geminiResponse) {
        try {
            JsonNode root = objectMapper.readTree(geminiResponse);
            String text = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
            // Remove markdown code block if present
            text = text.replace("```json", "").replace("```", "").trim();
            return text;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }
}