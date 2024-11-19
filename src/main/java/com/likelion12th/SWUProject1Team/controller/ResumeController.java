package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.Dto.CertificateDto;
import com.likelion12th.SWUProject1Team.Dto.ResumeDto;
import com.likelion12th.SWUProject1Team.entity.AcademicInfo;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import com.likelion12th.SWUProject1Team.Repository.AddressRepository;
import com.likelion12th.SWUProject1Team.Service.AcademicInfoService;
import com.likelion12th.SWUProject1Team.Service.ResumeService;
import com.likelion12th.SWUProject1Team.Service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final AddressRepository addressRepository;
    private final AcademicInfoService academicInfoService; // 주입 추가
    private final WorkExperienceService workExperienceService;

    @PostMapping("/init/{userId}")
    public ResponseEntity<ResumeDto> initializeResumeForUser(@PathVariable Integer userId) {
        // 서비스 호출
        ResumeDto createdResume = resumeService.initializeResumeForUser(userId);

        // 반환 값 확인
        if (createdResume == null) {
            throw new IllegalArgumentException("Failed to create resume for userId: " + userId);
        }

        // 응답 반환
        return ResponseEntity.ok(createdResume);
    }

    // 이력서 생성
    @PostMapping("/create")
    public ResponseEntity<ResumeDto> createResume(
            @RequestBody ResumeDto resumeDto,
            @RequestParam Integer memberId) {
        ResumeDto createdResume = resumeService.createResume(resumeDto, memberId);
        return ResponseEntity.ok(createdResume);
    }

    /*

    @PostMapping("/{resumeId}/save-title")
    public ResponseEntity<String> saveTitle(@PathVariable Long resumeId, @RequestParam String title) {
        resumeService.saveTitle(resumeId, title);
        return ResponseEntity.ok("제목이 성공적으로 저장되었습니다.");
    }

     */

    @PostMapping("/{resumeId}/add-additional-fields")
    public ResponseEntity<ResumeDto> addAdditionalFields(
            @PathVariable Long resumeId,
            @RequestBody ResumeDto resumeDto) {
        try {
            ResumeDto updatedResume = resumeService.addAdditionalFields(resumeId, resumeDto);
            return ResponseEntity.ok(updatedResume);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 이력서 조회 (resumeId로 조회) - ?
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long resumeId) {
        try {
            ResumeDto resumeDto = resumeService.getResume(resumeId);
            return ResponseEntity.ok(resumeDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 이력서 전체 조회
    @GetMapping("/member/{memberId}/full-details")
    public ResponseEntity<List<ResumeDto>> getAllResumesByMemberId(@PathVariable Integer memberId) {
        List<ResumeDto> resumes = resumeService.getAllResumesByMemberId(memberId);
        return ResponseEntity.ok(resumes);
    }

    // 특정 회원의 모든 이력서 조회 (memberId로 조회)
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ResumeDto>> getResumesByMemberId(@PathVariable Integer memberId) {
        List<ResumeDto> resumes = resumeService.getResumesByMemberId(memberId);
        return ResponseEntity.ok(resumes);
    }

    // 특정 회원의 최신 이력서 조회
    @GetMapping("/member/{memberId}/latest")
    public ResponseEntity<ResumeDto> getLatestResumeByMemberId(@PathVariable Integer memberId) {
        ResumeDto latestResume = resumeService.getLatestResumeByMemberId(memberId);
        return ResponseEntity.ok(latestResume);
    }


    /*
    // 제목 저장 (OCR 인식으로 입력)
    @PostMapping("/save-title-ocr")
    public ResponseEntity<String> saveTitleFromOcr(@RequestParam("file") MultipartFile file, @RequestParam Integer memberId) {
        try {
            byte[] imageBytes = file.getBytes();
            resumeService.saveTitleFromOcr(imageBytes, memberId);
            return ResponseEntity.ok("OCR로 제목이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("OCR 제목 저장 중 오류가 발생했습니다.");
        }
    }

     */

    // 이력서와 사용자 정보를 가져오는 API (이력서 - 개인정보 작성하기 조회 부분)
    @GetMapping("/{memberId}/personal-info-edit")
    public ResponseEntity<Map<String, Object>> getPersonalInfo(@PathVariable Integer memberId) {
        // 사용자의 회원 정보 가져오기
        Member member = resumeService.getMemberInfo(memberId);
        // 사용자의 이력서 정보 가져오기 (프로필 사진 포함)
        Resume resume = resumeService.getResumeWithMemberInfo(memberId);

        if (member != null) {
            // 로그로 데이터 확인
            System.out.println("Member 데이터: " + member);

            Map<String, Object> response = new HashMap<>();
            response.put("name", member.getName());
            response.put("email", member.getEmail());
            response.put("phone_number", member.getPhone_number());
            response.put("profileImage", resume.getProfileImage());
            response.put("postcode", resume.getPostcode());
            response.put("roadAddress", resume.getRoadAddress());
            response.put("detailAddress", resume.getDetailAddress());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 프로필 이미지 업로드 API
    @PostMapping("/{memberId}/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Integer memberId,
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Member ID: " + memberId); // 입력받은 Member ID 확인
            System.out.println("File Name: " + file.getOriginalFilename()); // 업로드된 파일 이름 확인

            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
            }

            // 프로필 이미지 저장
            String filePath = resumeService.saveProfileImage(memberId, file);
            System.out.println("File saved at path: " + filePath); // 파일 저장 경로 출력


            if (filePath != null) {
                return ResponseEntity.ok("프로필 이미지가 성공적으로 업로드되었습니다. 경로: " + filePath);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류 발생");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 이미지 업로드 실패: " + e.getMessage());
        }
    }

    // 주소 저장 API
    @PostMapping("/{memberId}/save-address")
    public ResponseEntity<String> saveAddress(
            @PathVariable Integer memberId,
            @RequestBody Map<String, String> addressData) {
        boolean isSaved = resumeService.saveAddress(memberId, addressData);
        if (isSaved) {
            return ResponseEntity.ok("주소가 저장되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주소 저장 실패");
        }
    }

    // 이력서 제목 + 개인 정보 조회용 API (이력서 페이지에서 작성한 내용 포함)
    @GetMapping("/{memberId}/personal-info-view")
    public ResponseEntity<Map<String, Object>> getPersonalInfoForView(@PathVariable Integer memberId) {
        try {
            Map<String, Object> personalInfo = resumeService.getPersonalInfo(memberId);
            return ResponseEntity.ok(personalInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 이력서와 학력 정보를 함께 조회하는 API
    @GetMapping("/{memberId}/resume-with-education")
    public ResponseEntity<Map<String, Object>> getResumeWithEducation(@PathVariable Integer memberId) {
        // 이력서 정보 조회
        Resume resume = resumeService.getResumeWithMemberInfo(memberId);

        if (resume == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 학력 정보 조회 시 Long 타입으로 변환
        List<AcademicInfo> academicInfos = academicInfoService.getAcademicInfoByResumeId(resume.getId().longValue());

        // 이력서와 학력 정보를 포함한 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("resume", resume);
        response.put("academicInfos", academicInfos);

        return ResponseEntity.ok(response);
    }

    // 특정 이력서의 총 경력과 경력 리스트를 조회하는 API
    @GetMapping("/{resumeId}/work-experiences")
    public ResponseEntity<Map<String, Object>> getWorkExperiences(@PathVariable Long resumeId) {
        Map<String, Object> response = new HashMap<>();

        // 경력 리스트 조회
        List<WorkExperience> experiences = workExperienceService.getWorkExperiences(resumeId);
        response.put("workExperiences", experiences);

        // 총 경력 기간 계산
        String totalExperience = workExperienceService.calculateTotalExperience(resumeId);
        response.put("totalExperience", totalExperience);

        return ResponseEntity.ok(response);
    }

    // 강점 저장 API
    @PostMapping("/{resumeId}/strengths")
    public ResponseEntity<String> updateStrengths(
            @PathVariable Long resumeId,
            @RequestBody List<String> strengths) {
        resumeService.updateStrengths(resumeId, strengths);
        return ResponseEntity.ok("성격 및 강점이 성공적으로 저장되었습니다.");
    }

    @GetMapping("/{resumeId}/strengths") // 강점 정보
    public ResponseEntity<List<String>> getStrengths(@PathVariable Long resumeId) {
        List<String> strengths = resumeService.getStrengths(resumeId);
        return ResponseEntity.ok(strengths);
    }

    @GetMapping("/{resumeId}/details") // 경력 사항, 강점 및 필드
    public ResponseEntity<ResumeDto> getResumeDetails(@PathVariable Long resumeId) {
        // 서비스에서 이력서 정보를 가져옵니다.
        ResumeDto resumeDto = resumeService.getResume(resumeId);
        return ResponseEntity.ok(resumeDto);
    }

    // 자격증 추가 API
    @PostMapping("/{resumeId}/certificates")
    public ResponseEntity<String> addCertificates(
            @PathVariable Long resumeId,
            @RequestBody List<CertificateDto> certificateDtos) {
        resumeService.addCertificates(resumeId, certificateDtos);
        return ResponseEntity.ok("자격증이 성공적으로 추가되었습니다.");
    }

    // 특정 이력서의 자격증 조회 API
    @GetMapping("/{resumeId}/certificates")
    public ResponseEntity<List<CertificateDto>> getCertificates(@PathVariable Long resumeId) {
        List<CertificateDto> certificates = resumeService.getCertificatesByResumeId(resumeId);
        return ResponseEntity.ok(certificates);
    }

    // 이력서 제목 수동 저장 API
    @PostMapping("/{memberId}/title/manual/")
    public ResponseEntity<String> saveManualTitle(
            @PathVariable Integer memberId, // 회원 ID
            @PathVariable Long resumeId,    // 이력서 ID
            @RequestParam String title) {   // 제목
        try {
            // 특정 이력서의 제목 수정
            resumeService.saveTitle(title, memberId, resumeId);
            return ResponseEntity.ok("이력서 제목이 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}