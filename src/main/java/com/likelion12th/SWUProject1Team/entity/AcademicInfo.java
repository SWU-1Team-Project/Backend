package com.likelion12th.SWUProject1Team.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "academic_info")
public class AcademicInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName; // 학교명 (고등학교 또는 대학교 검색 시 사용)
    private String schoolGubun; // 외부 API에서 가져오는 학교 구분 (예: 고등학교, 대학교)
    private String type; // 사용자 입력 데이터 구분 (예: 고등학교, 대학교, 대학원)
    private String region; // 지역
    private String address; // 필드명 'adres' -> 'address'로 변경 : 주소

    private String graduateSchoolName; // 대학원 명 (기관명)
    private String major;              // 전공명 / 과정

    // 데이터 출처: api 또는 user
    private String dataSource;
    private String startDate; // 재학 시작 기간
    private String endDate; // 종료 기간
    private String graduationStatus; // 졸업 여부

    // Resume 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    @JsonBackReference // 자식 -> 부모 방향 순환 참조 방지
    private Resume resume; // 이 필드를 추가하여 오류 해결

    // 요청에서 resumeId를 받아오기 위한 임시 필드
    @Transient
    private Long resumeId; // 클라이언트 요청에서 이력서 ID를 받아오기 위해 추가
}

