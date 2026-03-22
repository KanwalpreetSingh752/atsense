package com.atsense.backend.repository;

import com.atsense.backend.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByResumeUserIdOrderByCreatedAtDesc(Long userId);
    long countByResumeUserId(Long userId);
}