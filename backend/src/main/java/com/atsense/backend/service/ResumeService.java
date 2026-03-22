package com.atsense.backend.service;

import com.atsense.backend.model.Resume;
import com.atsense.backend.model.User;
import com.atsense.backend.repository.ResumeRepository;
import com.atsense.backend.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ResumeService {

    @Autowired private ResumeRepository resumeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CloudinaryService cloudinaryService;

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public Resume saveResume(MultipartFile file, String versionName, String userEmail)
            throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String parsedText = extractTextFromPdf(file);
        String fileUrl = cloudinaryService.uploadFile(file);
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setVersionName(versionName != null ? versionName : "Resume");
        resume.setFileUrl(fileUrl);
        resume.setParsedText(parsedText);
        return resumeRepository.save(resume);
    }

    public List<Resume> getUserResumes(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resumeRepository.findByUser(user);
    }

    public void deleteResume(Long id, String userEmail) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        // Make sure the resume belongs to the user requesting deletion
        if (!resume.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to delete this resume");
        }
        resumeRepository.deleteById(id);
    }
}


