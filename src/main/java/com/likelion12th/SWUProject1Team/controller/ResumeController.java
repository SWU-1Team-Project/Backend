package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.dto.CertificateDto;
import com.likelion12th.SWUProject1Team.dto.CustomUserDetails;
import com.likelion12th.SWUProject1Team.dto.ResumeDto;
import com.likelion12th.SWUProject1Team.dto.ResumeRequestDto;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import com.likelion12th.SWUProject1Team.repository.AddressRepository;
import com.likelion12th.SWUProject1Team.repository.MemberRepository;
import com.likelion12th.SWUProject1Team.repository.ResumeRepository;
import com.likelion12th.SWUProject1Team.service.AcademicInfoService;
import com.likelion12th.SWUProject1Team.service.SectionService;
import com.likelion12th.SWUProject1Team.service.ResumeService;
import com.likelion12th.SWUProject1Team.service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ResumeRepository resumeRepository;
    private final AcademicInfoService academicInfoService; // 주입 추가
    private final WorkExperienceService workExperienceService;
    private final MemberRepository memberRepository;
    private final SectionService sectionService; // 의존성 주입

    // 체크용 api
    @GetMapping("/check-user")
    public ResponseEntity<Map<String, Object>> checkUserCreation(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            // username 가져오기
            String username = userDetails.getUsername();

            // username으로 Member 객체 조회
            Member member = memberRepository.findByUsername(username);

            if (member != null) {
                response.put("status", "success");
                response.put("message", "사용자 정보 확인 완료");
                response.put("userId", member.getMemberId());
            } else {
                response.put("status", "failure");
                response.put("message", "사용자를 찾을 수 없습니다");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "failure");
            response.put("message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 필수 항목 작성 완료한 후 생성하기
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRequiredResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ResumeRequestDto request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // username 가져오기
            String username = userDetails.getUsername();

            // MemberRepository를 사용해 username으로 Member 조회
            Member member = memberRepository.findByUsername(username);

            // userId 가져오기
            int userId = member.getMemberId();

            // Resume 생성
            Resume createdResume = resumeService.createResumeForMember(userId, request);

            // 성공 응답 구성
            response.put("status", "success");
            response.put("message", "이력서 생성 완료!");
            response.put("resumeId", createdResume.getId());
            response.put("title", createdResume.getTitle());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "이력서 생성 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 이력서 조회 시에 사용
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> getResumeById(@PathVariable Integer resumeId) {
        ResumeDto resume = resumeService.getResumeById(resumeId);
        return ResponseEntity.ok(resume);
    }

    // 특정 User의 모든 Resume 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeDto>> getResumesByUserId(@PathVariable int userId) {
        List<ResumeDto> resumes = resumeService.getResumesByUserId(userId);
        return ResponseEntity.ok(resumes);
    }

    // 이력서 수정하기
    @PutMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> updateResume(
            @PathVariable Integer resumeId,
            @RequestBody ResumeRequestDto request) {
        ResumeDto updatedResume = resumeService.updateResume(resumeId, request);
        return ResponseEntity.ok(updatedResume);
    }

    @GetMapping("/{resumeId}/details")
    public ResponseEntity<ResumeDto> getCompleteResumeDetails(@PathVariable Integer resumeId) {
        ResumeDto resumeDto = resumeService.getCompleteResumeById(resumeId);
        return ResponseEntity.ok(resumeDto);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> deleteResume(@PathVariable Integer resumeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            resumeService.deleteResume(resumeId);
            response.put("status", "success");
            response.put("message", "이력서가 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("status", "failure");
            response.put("message", "이력서 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 프로필 이미지 업로드 API
    @PostMapping("/{userId}/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Member ID: " + userId); // 입력받은 Member ID 확인
            System.out.println("File Name: " + file.getOriginalFilename()); // 업로드된 파일 이름 확인

            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
            }

            // 프로필 이미지 저장
            String filePath = resumeService.saveProfileImage(userId, file);
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

    // 자격증 추가하기
    @PostMapping("/{resumeId}/certificates")
    public ResponseEntity<String> addCertificates(
            @PathVariable Integer resumeId,
            @RequestBody List<CertificateDto> certificateDtos) {
        resumeService.addCertificates(resumeId, certificateDtos);
        return ResponseEntity.ok("자격증이 성공적으로 추가되었습니다.");
    }

    // 성장 과정 저장
    @PostMapping("/{resumeId}/sections/growth-process")
    public ResponseEntity<Map<String, String>> saveGrowthProcess(
            @PathVariable Integer resumeId,
            @RequestBody Map<String, String> request) {
        sectionService.saveSection(resumeId, "growthProcess", request.get("text"));
        return ResponseEntity.ok(Map.of("status", "success", "message", "성장 과정이 성공적으로 저장되었습니다."));
    }

    // 성격 소개 저장
    @PostMapping("/{resumeId}/sections/personality-introduction")
    public ResponseEntity<Map<String, String>> savePersonalityIntroduction(
            @PathVariable Integer resumeId,
            @RequestBody Map<String, String> request) {
        sectionService.saveSection(resumeId, "personalityIntroduction", request.get("text"));
        return ResponseEntity.ok(Map.of("status", "success", "message", "성격 소개가 성공적으로 저장되었습니다."));
    }

    // 지원 동기 저장
    @PostMapping("/{resumeId}/sections/motivation")
    public ResponseEntity<Map<String, String>> saveMotivation(
            @PathVariable Integer resumeId,
            @RequestBody Map<String, String> request) {
        sectionService.saveSection(resumeId, "motivation", request.get("text"));
        return ResponseEntity.ok(Map.of("status", "success", "message", "지원 동기가 성공적으로 저장되었습니다."));
    }

    // 희망 업무 및 포부 저장
    @PostMapping("/{resumeId}/sections/job-ambition")
    public ResponseEntity<Map<String, String>> saveJobAmbition(
            @PathVariable Integer resumeId,
            @RequestBody Map<String, String> request) {
        sectionService.saveSection(resumeId, "jobAmbition", request.get("text"));
        return ResponseEntity.ok(Map.of("status", "success", "message", "희망 업무 및 포부가 성공적으로 저장되었습니다."));
    }

    // 특기 사항 저장
    @PostMapping("/{resumeId}/sections/special-note")
    public ResponseEntity<Map<String, String>> saveSpecialNote(
            @PathVariable Integer resumeId,
            @RequestBody Map<String, String> request) {
        sectionService.saveSection(resumeId, "specialNote", request.get("text"));
        return ResponseEntity.ok(Map.of("status", "success", "message", "특기 사항이 성공적으로 저장되었습니다."));
    }

    // 추가 항목 저장하기
    @PostMapping("/{resumeId}/add-additional-fields")
    public ResponseEntity<ResumeDto> addAdditionalFields(
            @PathVariable Integer resumeId,
            @RequestBody ResumeDto resumeDto) {
        try {
            ResumeDto updatedResume = resumeService.addAdditionalFields(resumeId, resumeDto);
            return ResponseEntity.ok(updatedResume);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

        @GetMapping("/{userId}/personal-info-edit")
        public ResponseEntity<Map<String, Object>> getPersonalInfo(@PathVariable int userId) {
            Member member = resumeService.getMemberInfo(userId);
            Resume resume = resumeService.getResumeWithMemberInfo(userId);

            if (member != null) {
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

    @GetMapping("/{resumeId}/work-experiences")
    public ResponseEntity<Map<String, Object>> getWorkExperiences(@PathVariable Integer resumeId) {
        Map<String, Object> response = new HashMap<>();
        List<WorkExperience> experiences = workExperienceService.getWorkExperiences(resumeId);
        response.put("workExperiences", experiences);

        String totalExperience = workExperienceService.calculateTotalExperience(resumeId);
        response.put("totalExperience", totalExperience);

        return ResponseEntity.ok(response);
    }


/*
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

    // 이력서 조회 (resumeId로 조회)
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long resumeId) {
        try {
            ResumeDto resumeDto = resumeService.getResume(resumeId);
            return ResponseEntity.ok(resumeDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 특정 회원의 최신 이력서 조회
    @GetMapping("/member/{memberId}/latest")
    public ResponseEntity<ResumeDto> getLatestResumeByMemberId(@PathVariable Integer memberId) {
        ResumeDto latestResume = resumeService.getLatestResumeByMemberId(memberId);
        return ResponseEntity.ok(latestResume);
    }

 */


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

    /*

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

    // 강점 업데이트 API
    @PostMapping("/{resumeId}/strengths")
    public ResponseEntity<String> updateStrengths(
            @PathVariable Long resumeId,
            @RequestBody List<String> strengths) {
        resumeService.updateStrengths(resumeId, strengths);
        return ResponseEntity.ok("성격 및 강점이 성공적으로 저장되었습니다.");
    }

    // 강점 가져오기
    @GetMapping("/{resumeId}/strengths")
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

    // 특정 이력서의 자격증 조회 API
    @GetMapping("/{resumeId}/certificates")
    public ResponseEntity<List<CertificateDto>> getCertificates(@PathVariable Long resumeId) {
        List<CertificateDto> certificates = resumeService.getCertificatesByResumeId(resumeId);
        return ResponseEntity.ok(certificates);
  }

     */
}