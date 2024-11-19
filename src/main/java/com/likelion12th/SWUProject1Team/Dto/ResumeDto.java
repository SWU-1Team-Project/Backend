package com.likelion12th.SWUProject1Team.Dto;

import com.likelion12th.SWUProject1Team.entity.AcademicInfo;
import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class ResumeDto {
    private Integer id;
    private String title;
    private String profileImage;
    private Boolean isExperienced; // 경력 여부 (true: 경력, false: 신입)
    private String name;
    private String phone_Number;
    private String email;
    private String gender; // 성별
    private String birth_day; //생년월일
    private String postcode;
    private String roadAddress;
    private String detailAddress;

    private List<AcademicInfo> academicInfoList;
    private List<WorkExperience> workExperienceList;
    private List<CertificateDto> certifications;
    // private List<Certification> certifications;
    private List<String> strengths;

    private String totalExperience; // 총 경력 추가

    // 추가 항목 필드
    private String growthProcess;
    private String personality;
    private String reason;
    private String job;
    private String specialNote;

}
