package com.atsense.backend.controller;

import com.atsense.backend.dto.JobProfileRequest;
import com.atsense.backend.model.AnalysisResult;
import com.atsense.backend.model.Resume;
import com.atsense.backend.service.AnalysisService;
import com.atsense.backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired private AnalysisService analysisService;
    @Autowired private ResumeService resumeService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeResume(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("versionName") String versionName,
            @RequestBody JobProfileRequest jobProfile,
            Authentication auth) throws Exception {
        Resume resume = resumeService.saveResume(resumeFile, versionName, auth.getName());
        AnalysisResult result = analysisService.analyze(resume, jobProfile, auth.getName());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AnalysisResult>> getHistory(Authentication auth) {
        return ResponseEntity.ok(analysisService.getHistory(auth.getName()));
    }

    @PostMapping("/cover-letter")
    public ResponseEntity<?> generateCoverLetter(
            @RequestParam Long resumeId,
            @RequestParam Long jobProfileId,
            Authentication auth) {
        return ResponseEntity.ok(analysisService.generateCoverLetter(resumeId, jobProfileId));
    }

    @PostMapping("/interview-questions")
    public ResponseEntity<?> getInterviewQuestions(
            @RequestParam Long resumeId,
            @RequestParam Long jobProfileId) {
        return ResponseEntity.ok(analysisService.predictInterviewQuestions(resumeId, jobProfileId));
    }

    @PostMapping("/career-paths")
    public ResponseEntity<?> getCareerPaths(@RequestParam Long resumeId) {
        return ResponseEntity.ok(analysisService.suggestCareerPaths(resumeId));
    }
}
