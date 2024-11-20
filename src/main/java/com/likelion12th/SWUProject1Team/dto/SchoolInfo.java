package com.likelion12th.SWUProject1Team.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 추가

@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolInfo {

    @JsonProperty("schoolName") // 고등학교 또는 대학교 검색용
    private String schoolName;

    @JsonProperty("schoolGubun")
    private String schoolGubun;

    @JsonProperty("region") // 지역
    private String region;

    @JsonProperty("adres") // JSON 응답에서 "adres"를 Java 필드 "address"에 매핑
    private String address; // 필드명 'adres' -> 'address'로 변경

    private String graduateSchoolName; // 대학원 명 (기관명)
    private String major;              // 전공명 / 과정
    private String startDate;       // 재학 시작 기간
    private String endDate;         // 재학 종료 기간
    private String graduationStatus;   // 졸업 여부 (졸업, 중퇴 등)
    private String dataSource;
    private String type; // 사용자 입력 데이터 구분 (예: 고등학교, 대학교, 대학원)

    // private String campusName; // 새 필드 추가
}
