package com.likelion12th.SWUProject1Team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor

public class WorkExperienceDto {
    private Integer resumeId;  // 추가된 resumeId 필드
    private String companyName;

    private LocalDate startDate; // 시작일
    private LocalDate endDate;   // 종료일

    private Boolean isCurrent;
    private String responsibilities;

    // 총 경력 기간 필드 추가
    private String totalExperience;

    // 유효성 검사 메서드 추가
    public void validate() {
        if (resumeId == null || resumeId <= 0) {
            throw new IllegalArgumentException("Resume ID cannot be null or zero");
        }
        if (companyName == null || companyName.isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (!isCurrent && endDate == null) {
            throw new IllegalArgumentException("End date cannot be null if not currently employed");
        }
    }

    public WorkExperienceDto(String companyName, LocalDate startDate, LocalDate endDate, String responsibilities) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.responsibilities = responsibilities;
    }
}
