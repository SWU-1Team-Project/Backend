package com.likelion12th.SWUProject1Team.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CertificateDto {
    private String name; // 자격증 이름
    private String date; // 자격증 취득일

    // 필요한 경우 명시적으로 생성자 추가
    public CertificateDto(String name, String date) {
        this.name = name;
        this.date = date;
    }
}

