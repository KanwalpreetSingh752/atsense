package com.atsense.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    private Integer fitScore;
    private Integer keywordScore;
    private Integer formattingScore;
    private Integer sectionScore;
    private Integer readabilityScore;

    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;

    private String verdict;
}