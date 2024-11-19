package com.likelion12th.SWUProject1Team.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PersonalInfoDto {
    private MultipartFile resumeImage; // 프로필 이미지
    private String name; // 이름
    private String phoneNumber; // 전화번호
    private String email; // 이메일

    // 광역시/도 정보
    private String ctprvnCd;    // 행정구역코드 (광역시/도)
    private String ctpKorNm;    // 광역시도명

    // 시/군/구 정보
    private String sigCd;       // 행정구역코드 (시/군/구)
    private String sigKorNm;    // 시군구명

    // 읍/면/동 정보
    private String emdCd;       // 행정구역코드 (읍/면/동)
    private String emdKorNm;    // 읍면동명

    private String detailedAddress;  // 상세 주소 입력
}