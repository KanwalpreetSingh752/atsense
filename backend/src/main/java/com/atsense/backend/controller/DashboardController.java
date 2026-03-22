package com.atsense.backend.controller;

import com.atsense.backend.model.AnalysisResult;
import com.atsense.backend.model.Resume;
import com.atsense.backend.model.UserBadge;
import com.atsense.backend.service.AnalysisService;
import com.atsense.backend.service.BadgeService;
import com.atsense.backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private AnalysisService analysisService;
    @Autowired private ResumeService resumeService;
    @Autowired private BadgeService badgeService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(Authentication auth) {
        String email = auth.getName();

        List<Resume> resumes = resumeService.getUserResumes(email);
        List<AnalysisResult> history = analysisService.getHistory(email);
        List<UserBadge> badges = badgeService.getUserBadges(email);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalResumes", resumes.size());
        summary.put("totalAnalyses", history.size());
        summary.put("badges", badges);

        // Latest ATS score
        if (!history.isEmpty()) {
            AnalysisResult latest = history.get(0);
            summary.put("latestFitScore", latest.getFitScore());
            summary.put("latestKeywordScore", latest.getKeywordScore());
            summary.put("latestFormattingScore", latest.getFormattingScore());
            summary.put("latestSectionScore", latest.getSectionScore());
            summary.put("latestReadabilityScore", latest.getReadabilityScore());
        }

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/score-history")
    public ResponseEntity<List<AnalysisResult>> getScoreHistory(Authentication auth) {
        return ResponseEntity.ok(analysisService.getHistory(auth.getName()));
    }

    @GetMapping("/badges")
    public ResponseEntity<List<UserBadge>> getBadges(Authentication auth) {
        return ResponseEntity.ok(badgeService.getUserBadges(auth.getName()));
    }
}