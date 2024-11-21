package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.dto.WorkExperienceDto;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import com.likelion12th.SWUProject1Team.repository.ResumeRepository;
import com.likelion12th.SWUProject1Team.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkExperienceService {

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    // 단일 WorkExperience 저장
    public void saveWorkExperience(WorkExperienceDto workExperienceDto) {
        saveWorkExperienceInternal(workExperienceDto);
        updateTotalExperience(workExperienceDto.getResumeId()); // 저장 후 총 경력 업데이트
    }

    // 여러 WorkExperience 저장
    public void saveWorkExperiences(List<WorkExperienceDto> workExperienceDtos) {
        workExperienceDtos.forEach(this::saveWorkExperienceInternal);
    }

    private void saveWorkExperienceInternal(WorkExperienceDto workExperienceDto) {
        try {
            workExperienceDto.validate();
            Resume resume = resumeRepository.findById(workExperienceDto.getResumeId())
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + workExperienceDto.getResumeId()));

            WorkExperience workExperience = new WorkExperience();
            workExperience.setCompanyName(workExperienceDto.getCompanyName());
            workExperience.setStartDate(workExperienceDto.getStartDate());
            workExperience.setEndDate(workExperienceDto.getEndDate());
            workExperience.setIsCurrent(workExperienceDto.getIsCurrent());
            workExperience.setResponsibilities(workExperienceDto.getResponsibilities());
            workExperience.setResume(resume);

            System.out.println("Saving work experience: " + workExperience);
            workExperienceRepository.save(workExperience);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save work experience: " + e.getMessage());
        }
    }


    // 경력 삭제
    public void deleteWorkExperience(Integer resumeId, Integer workExperienceId) {
        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new IllegalArgumentException("경력 사항을 찾을 수 없습니다."));

        if (!workExperience.getResume().getId().equals(resumeId)) {
            throw new IllegalArgumentException("이력서와 경력이 일치하지 않습니다.");
        }

        workExperienceRepository.delete(workExperience);

        updateTotalExperience(resumeId); // 삭제 후 총 경력 업데이트
    }

    public void updateTotalExperience(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        // 총 경력 기간 계산
        String totalExperience = calculateTotalExperience(resumeId);
        resume.setTotalExperience(totalExperience);
        resumeRepository.save(resume);
    }


    // 총 경력 기간 계산
    public String calculateTotalExperience(Integer resumeId) {
        List<WorkExperience> experiences = workExperienceRepository.findByResumeId(resumeId);

        int totalMonths = experiences.stream()
                .mapToInt(WorkExperience::calculateDurationInMonths)
                .sum();

        int years = totalMonths / 12;
        int months = totalMonths % 12;

        // "00년 00개월" 형식으로 반환
        return years + "년 " + months + "개월";
    }

    // 특정 이력서의 모든 경력 조회
    public List<WorkExperience> getWorkExperiences(Integer resumeId) {
        return workExperienceRepository.findByResume_Id(resumeId);
    }
}


    // 내부적으로 WorkExperience 저장 처리하는 메서드

    /*
    private void saveWorkExperienceInternal(WorkExperienceDto workExperienceDto) {
        if (workExperienceDto == null) {
            throw new IllegalArgumentException("WorkExperienceDto cannot be null");
        }

        workExperienceDto.validate();

        WorkExperience workExperience = new WorkExperience();
        workExperience.setCompanyName(workExperienceDto.getCompanyName());
        workExperience.setStartDate(workExperienceDto.getStartDate());
        workExperience.setEndDate(workExperienceDto.getEndDate());
        workExperience.setIsCurrent(workExperienceDto.getIsCurrent());
        workExperience.setResponsibilities(workExperienceDto.getResponsibilities());

        Resume resume = resumeRepository.findById(workExperienceDto.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + workExperienceDto.getResumeId()));

        workExperience.setResume(resume);

        workExperienceRepository.save(workExperience);
    }

     */

    // 총 경력 기간 계산
