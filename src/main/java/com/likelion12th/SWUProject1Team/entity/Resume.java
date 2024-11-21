package com.likelion12th.SWUProject1Team.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "resume")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Integer id; // 기본 키 필드

    // 단방향 관계 설정: Resume -> Member
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // 순환 참조 방지
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title; // 이력서 제목
    private String userId; // 사용자의 ID
    private String profileImage; // 사용자 프로필 이미지
    private Boolean isExperienced; // 경력 여부 저장 (경력 / 신입인지 구분에 사용)
    private String titleImage; // 제목 이미지

    private LocalDateTime modifiedDate; // 이력서 최종 수정 날짜

    @ElementCollection
    @CollectionTable(name = "strengths", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "strength")
    private List<String> strengths; // 강점 목록 저장 필드

    // 총 경력 기간 필드 추가
    private String totalExperience;

    // 주소 관련 필드 추가
    @Column
    private String postcode; // 우편번호

    @Column
    private String roadAddress; // 도로명 주소

    @Column
    private String detailAddress; // 상세 주소

    // 전체 주소를 하나의 문자열로 반환하는 메소드
    public String getFullAddress() {

        return postcode + " " + roadAddress + " " + detailAddress;
    }

    // 사진을 찍은 후 OCR로 인식한 제목을 저장하는 메소드
    public void setTitleFromOCR(String ocrTitle) {
        this.title = ocrTitle;
    }

    // 단방향 관계: WorkExperience -> Resume (WorkExperience: 경력 사항)
    // 경력 사항 List
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WorkExperience> workExperienceList;

    // 단방향 관계: AcademicInfo -> Resume (AcademicInfo: 학력 정보)
    // 학력 정보 List
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AcademicInfo> academicInfoList;

    // 자격증 List (Certification : 자격증 정보)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Certification> certifications;

    // 추가 항목 필드
    private String growthProcess; // 성장 과정
    private String personality; // 성격 소개
    private String reason; // 지원 동기
    private String job; // 희망 업무 및 포부
    private String specialNote; // 특기 사항

}
