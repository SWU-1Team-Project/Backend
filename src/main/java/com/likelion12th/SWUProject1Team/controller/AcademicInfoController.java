package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.Dto.SchoolInfo;
import com.likelion12th.SWUProject1Team.entity.AcademicInfo;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.Repository.ResumeRepository;
import com.likelion12th.SWUProject1Team.Service.AcademicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resumes/{resumeId}/academic-info")
public class AcademicInfoController {

    @Autowired
    private AcademicInfoService academicInfoService;

    @Autowired
    private ResumeRepository resumeRepository;

    // 1. 로컬 데이터베이스에서 학교 이름을 검색하여 SchoolInfo DTO 형식으로 반환
    @GetMapping("/search")
    public ResponseEntity<List<SchoolInfo>> searchSchool(
            @PathVariable Long resumeId,
            @RequestParam String schoolName,
            @RequestParam(required = false) String type) {
        List<AcademicInfo> schoolInfoList = academicInfoService.searchSchoolFromLocal(schoolName);

        // AcademicInfo를 SchoolInfo로 변환
        List<SchoolInfo> schoolInfos = schoolInfoList.stream()
                .map(info -> {
                    SchoolInfo schoolInfo = new SchoolInfo();
                    schoolInfo.setSchoolName(info.getSchoolName());
                    schoolInfo.setSchoolGubun(info.getSchoolGubun());
                    schoolInfo.setRegion(info.getRegion());
                    schoolInfo.setAddress(info.getAddress());
                    schoolInfo.setStartDate(info.getStartDate());
                    schoolInfo.setEndDate(info.getEndDate());
                    schoolInfo.setGraduationStatus(info.getGraduationStatus());
                    schoolInfo.setGraduateSchoolName(info.getGraduateSchoolName());
                    schoolInfo.setMajor(info.getMajor());
                    schoolInfo.setType(info.getType());
                    return schoolInfo;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(schoolInfos);
    }

    // 2. 외부 API를 통해 모든 학교 정보를 가져와 저장하는 엔드포인트
    @GetMapping("/fetch-schools")
    public ResponseEntity<String> fetchSchools() {
        academicInfoService.fetchAndSaveAllSchools();
        return ResponseEntity.ok("School data fetched and saved successfully.");
    }

    // 3. 외부 API에서 특정 학교 이름으로 학교 정보를 검색하는 엔드포인트
    @GetMapping("/external-search")
    public ResponseEntity<List<SchoolInfo>> getExternalSchoolInfo(@RequestParam String schoolName) {
        List<SchoolInfo> schoolInfoList = academicInfoService.getSchoolInfo(schoolName);
        return ResponseEntity.ok(schoolInfoList);
    }

    // 특정 이력서에 학력 정보 추가
    @PostMapping("/add")
    public ResponseEntity<String> addAcademicInfo(@PathVariable Long resumeId, @RequestBody AcademicInfo academicInfo) {
        System.out.println("Received AcademicInfo: " + academicInfo);

        // 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));


        // 학력 정보 저장
        academicInfo.setDataSource("user"); // 사용자가 입력한 데이터 표시
        academicInfoService.saveAcademicInfo(resumeId, academicInfo);

        return ResponseEntity.ok("학력 정보가 성공적으로 저장되었습니다.");
    }

    /*
    @PostMapping("/add")
    public ResponseEntity<String> addAcademicInfo(@RequestBody AcademicInfo academicInfo, @RequestParam Long resumeId) {
        System.out.println("Received AcademicInfo: " + academicInfo);
        System.out.println("Received resumeId: " + resumeId);

        // 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        // 학력 정보 저장
        academicInfo.setResume(resume);
        academicInfo.setDataSource("user"); // 사용자가 입력한 데이터 표시
        academicInfoService.saveAcademicInfo(academicInfo);

        return ResponseEntity.ok("학력 정보가 성공적으로 저장되었습니다.");
    }

     */

    /*
    // 4. AcademicInfo 객체 하나를 추가하는 엔드포인트
    @PostMapping("/add")
    public ResponseEntity<String> addAcademicInfo(@RequestBody AcademicInfo academicInfo) {
        academicInfoService.saveAcademicInfo(academicInfo);
        return ResponseEntity.ok("학력 정보가 성공적으로 저장되었습니다.");
    }

     */

    // 5. 대학원 정보를 추가하는 엔드포인트 (상세 학력 정보 저장)
    @PostMapping("/add-graduate")
    public ResponseEntity<String> addGraduateInfo(@RequestBody SchoolInfo schoolInfo) {
        academicInfoService.saveAcademicInfo(schoolInfo, "graduate", "user");
        return ResponseEntity.ok("대학원 정보가 성공적으로 저장되었습니다.");
    }
}
