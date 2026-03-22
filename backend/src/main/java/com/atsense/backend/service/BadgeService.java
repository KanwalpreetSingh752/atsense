package com.atsense.backend.service;

import com.atsense.backend.model.AnalysisResult;
import com.atsense.backend.model.User;
import com.atsense.backend.model.UserBadge;
import com.atsense.backend.repository.UserBadgeRepository;
import com.atsense.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeService {

    @Autowired private UserBadgeRepository userBadgeRepository;
    @Autowired private UserRepository userRepository;

    public List<UserBadge> getUserBadges(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userBadgeRepository.findByUser(user);
    }

    public void checkAndAwardBadges(User user, AnalysisResult result, long totalAnalyses) {

        // First analysis badge
        if (totalAnalyses == 1) {
            awardBadge(user, "First Steps");
        }

        // ATS Champion - keyword score 90+
        if (result.getKeywordScore() >= 90) {
            awardBadge(user, "ATS Champion");
        }

        // Perfect Format badge
        if (result.getFormattingScore() == 100) {
            awardBadge(user, "Perfect Format");
        }

        // High Achiever - fit score 85+
        if (result.getFitScore() >= 85) {
            awardBadge(user, "High Achiever");
        }

        // Complete Profile - all sections score 90+
        if (result.getSectionScore() >= 90) {
            awardBadge(user, "Complete Profile");
        }
    }

    private void awardBadge(User user, String badgeName) {
        boolean alreadyHas = userBadgeRepository.existsByUserAndBadgeName(user, badgeName);
        if (!alreadyHas) {
            UserBadge badge = new UserBadge();
            badge.setUser(user);
            badge.setBadgeName(badgeName);
            userBadgeRepository.save(badge);
        }
    }
}