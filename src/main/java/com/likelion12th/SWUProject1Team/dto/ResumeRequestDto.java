package com.likelion12th.SWUProject1Team.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class ResumeRequestDto {
        private int memberId;
        private String title; // 이력서 제목
        private String profileImage; // 프로필 이미지 URL
        private Boolean isExperienced; // 경력 여부 (true: 경력, false: 신입)

        private String postcode; // 우편번호
        private String roadAddress; // 도로명 주소
        private String detailAddress; // 상세 주소

        private List<AcademicInfoDto> academicInfoList; // 학력 정보
        private List<WorkExperienceDto> workExperienceList; // 경력 정보 (경력일 경우만 필수)
        private List<String> strengths; // 성격 및 강점

        // 추가 항목 (선택 사항)
        private String growthProcess; // 성장 과정
        private String personality; // 성격 소개
        private String reason; // 지원 동기
        private String job; // 희망 업무 및 포부
        private String specialNote; // 특기 사항
    }

