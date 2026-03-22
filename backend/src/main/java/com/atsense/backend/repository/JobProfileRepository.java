package com.atsense.backend.repository;

import com.atsense.backend.model.JobProfile;
import com.atsense.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobProfileRepository extends JpaRepository<JobProfile, Long> {
    List<JobProfile> findByUser(User user);
    List<JobProfile> findByUserId(Long userId);
}