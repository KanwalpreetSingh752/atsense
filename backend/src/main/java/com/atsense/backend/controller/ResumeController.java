package com.atsense.backend.controller;

import com.atsense.backend.model.Resume;
import com.atsense.backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("resume") MultipartFile file,
            @RequestParam(value = "versionName", defaultValue = "Resume") String versionName,
            Authentication auth) throws Exception {
        Resume resume = resumeService.saveResume(file, versionName, auth.getName());
        return ResponseEntity.ok(resume);
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes(Authentication auth) {
        return ResponseEntity.ok(resumeService.getUserResumes(auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id, Authentication auth) {
        resumeService.deleteResume(id, auth.getName());
        return ResponseEntity.ok("Resume deleted successfully");
    }
}