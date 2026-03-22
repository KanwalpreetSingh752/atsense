package com.atsense.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class JobProfileRequest {
    private String title;
    private List<String> skills;
    private Integer experienceYears;
    private String education;
    private String industry;
}
