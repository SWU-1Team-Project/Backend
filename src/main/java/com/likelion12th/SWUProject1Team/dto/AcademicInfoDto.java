package com.likelion12th.SWUProject1Team.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AcademicInfoDto {
    private String schoolName; // 학교명 (고등학교 또는 대학교 검색 시 사용)
    private String schoolGubun; // 학교 구분 (예: 고등학교, 대학교)
    private String type; // 사용자 입력 데이터 구분 (예: 고등학교, 대학교, 대학원)
    private String region; // 지역
    private String address; // 주소

    private String graduateSchoolName; // 대학원 명
    private String major; // 대학원 전공명 / 과정

    private String dataSource; // 데이터 출처 (api 또는 user)
    private String startDate; // 재학 시작 기간
    private String endDate; // 종료 기간
    private String graduationStatus; // 졸업 여부 (재학 중 / 휴학 / 졸업 / 자퇴 중에서 선택)

    }
