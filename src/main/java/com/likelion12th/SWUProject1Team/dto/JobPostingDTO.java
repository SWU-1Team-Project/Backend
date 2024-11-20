package com.likelion12th.SWUProject1Team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class JobPostingDTO {
    private Integer jobPostingId;   // 채용 공고 ID
    private String companyName;     // 회사명
    private String jobTitle;        // 직무 제목
    private String hiringStatus;    // 채용 상태
    private String interestCount;   // 관심 수
    private String location;        // 위치
    private String contactInfo;     // 연락처
}