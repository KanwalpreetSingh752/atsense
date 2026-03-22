package com.atsense.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @ElementCollection
    @CollectionTable(name = "job_profile_skills", joinColumns = @JoinColumn(name = "job_profile_id"))
    @Column(name = "skill")
    private List<String> skills;

    private Integer experienceYears;
    private String education;
    private String industry;

    private LocalDateTime createdAt = LocalDateTime.now();
}

