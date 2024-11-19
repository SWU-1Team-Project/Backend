package com.likelion12th.SWUProject1Team.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Entity
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName; // 회사명
    private LocalDate startDate;  // 시작일
    private LocalDate endDate;    // 종료일
    private Boolean isCurrent;    // 현재 재직 중 여부
    private String responsibilities; // 담당 업무

    // Resume 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", referencedColumnName = "resume_id", nullable = false)
    @JsonBackReference
    private Resume resume;

    // 총 경력 기간 필드 추가
    private String totalExperience;

    // 근무 기간을 개월 수로 계산하는 메서드
    public int calculateDurationInMonths() {
        LocalDate end = (isCurrent != null && isCurrent) ? LocalDate.now() : endDate;
        if (startDate != null && end != null) {
            return (int) ChronoUnit.MONTHS.between(startDate, end);
        }
        return 0; // 시작일 또는 종료일이 없는 경우, 0개월로 반환
    }
}
