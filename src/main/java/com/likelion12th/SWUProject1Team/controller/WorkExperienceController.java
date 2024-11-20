package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.dto.WorkExperienceDto;
import com.likelion12th.SWUProject1Team.service.WorkExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes/{resumeId}/work-experiences")
public class WorkExperienceController {

    @Autowired
    private WorkExperienceService workExperienceService;

    // 단일 경력 추가
    @PostMapping
    public ResponseEntity<String> addWorkExperience(@PathVariable Long resumeId, @RequestBody WorkExperienceDto workExperienceDto) {
        workExperienceDto.setResumeId(resumeId);  // resumeId 설정
        workExperienceService.saveWorkExperience(workExperienceDto);
        return ResponseEntity.ok("경력 사항이 성공적으로 추가되었습니다");
    }

    // 여러 경력 정보 저장
    @PostMapping("/batchsave")
    public ResponseEntity<String> saveExperiences(@PathVariable Long resumeId, @RequestBody List<WorkExperienceDto> experiences) {
        System.out.println("Received experiences: " + experiences);
        experiences.forEach(exp -> exp.setResumeId(resumeId));  // 각 DTO에 resumeId 설정
        workExperienceService.saveWorkExperiences(experiences);
        return ResponseEntity.ok("경력 정보가 저장되었습니다.");
    }

    // 총 경력 조회
    @GetMapping("/total")
    public ResponseEntity<String> getTotalWorkExperience(@PathVariable Long resumeId) {
        String totalExperience = workExperienceService.calculateTotalExperience(resumeId);
        return ResponseEntity.ok("총 경력 기간: " + totalExperience);
    }

    // 단일 경력 삭제
    @DeleteMapping("/delete/{workExperienceId}")
    public ResponseEntity<String> deleteWorkExperience(@PathVariable Long resumeId, @PathVariable Long workExperienceId) {
        workExperienceService.deleteWorkExperience(resumeId, workExperienceId);
        return ResponseEntity.ok("경력 사항이 성공적으로 삭제되었습니다.");
    }

}
