package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

    @Service
    @RequiredArgsConstructor
    public class SectionService {

        private final ResumeRepository resumeRepository;

        // 1. 특정 섹션 조회
        public String getSection(Integer resumeId, String sectionName) {
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

            switch (sectionName) {
                case "growthProcess":
                    return resume.getGrowthProcess();
                case "personalityIntroduction":
                    return resume.getPersonality();
                case "motivation":
                    return resume.getReason();
                case "jobAmbition":
                    return resume.getJob();
                case "specialNote":
                    return resume.getSpecialNote();
                default:
                    throw new IllegalArgumentException("Invalid section name: " + sectionName);
            }
        }

        // 2. 특정 섹션 저장
        @Transactional
        public void saveSection(Integer resumeId, String sectionName, String text) {
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

            switch (sectionName) {
                case "growthProcess":
                    resume.setGrowthProcess(text);
                    break;
                case "personalityIntroduction":
                    resume.setPersonality(text);
                    break;
                case "motivation":
                    resume.setReason(text);
                    break;
                case "jobAmbition":
                    resume.setJob(text);
                    break;
                case "specialNote":
                    resume.setSpecialNote(text);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid section name: " + sectionName);
            }

            resumeRepository.save(resume);
        }

        // 3. OCR 텍스트 추출
        public String processImageForText(MultipartFile file) {
            return "OCR 추출된 텍스트"; // OCR 로직 또는 API 호출 구현
        }

        // 4. 맞춤법 검사
        public String checkSpelling(String text) {
            return "수정된 텍스트"; // 맞춤법 검사 API 호출 구현
        }
    }