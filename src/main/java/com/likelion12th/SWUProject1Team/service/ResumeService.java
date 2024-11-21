package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.dto.*;
import com.likelion12th.SWUProject1Team.entity.*;
import com.likelion12th.SWUProject1Team.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final CertificateRepository certificateRepository;

    @Value("${upload.path}")
    private String uploadPath;

    private final WorkExperienceService workExperienceService;
    private final WorkExperienceRepository workExperienceRepository;
    private final AcademicInfoRepository academicInfoRepository;
    private final MemberRepository memberRepository;
    private final OcrApiService ocrApiService;

    private AcademicInfo convertToAcademicInfo(AcademicInfoDto dto, Resume resume) {
        return new AcademicInfo(dto, resume);
    }

    // Dto를 WorkExperience 엔티티로 변환
    private WorkExperience convertToWorkExperience(WorkExperienceDto dto, Resume resume) {
        return new WorkExperience(dto, resume);
    }

    // 이력서 필수 항목 생성하기
    @Transactional
    public Resume createResumeForMember(int userId, ResumeRequestDto request) {
        // 1. Member 정보 가져오기
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + userId));

        validateRequiredFields(request);

        // 2. Resume 생성
        Resume resume = new Resume();
        resume.setMember(member);
        resume.setTitle(request.getTitle());
        resume.setProfileImage(request.getProfileImage());
        resume.setPostcode(request.getPostcode());
        resume.setRoadAddress(request.getRoadAddress());
        resume.setDetailAddress(request.getDetailAddress());
        resume.setIsExperienced(request.getIsExperienced());
        resume.setStrengths(request.getStrengths());

        // 3. AcademicInfo 설정
        List<AcademicInfo> academicInfos = request.getAcademicInfoList().stream()
                .map(dto -> convertToAcademicInfo(dto, resume))
                .collect(Collectors.toList());
        resume.setAcademicInfoList(academicInfos);

        // 4. WorkExperience 설정
        if (Boolean.TRUE.equals(request.getIsExperienced())) {
            List<WorkExperience> workExperiences = request.getWorkExperienceList().stream()
                    .map(dto -> convertToWorkExperience(dto, resume))
                    .collect(Collectors.toList());
            resume.setWorkExperienceList(workExperiences);
        } else {
            resume.setWorkExperienceList(Collections.emptyList());
        }

        // 5. Resume 저장
        return resumeRepository.save(resume);
    }

    private void validateRequiredFields(ResumeRequestDto request) {
        if (request.getTitle() == null || request.getPostcode() == null || request.getRoadAddress() == null
                || request.getDetailAddress() == null || request.getStrengths() == null || request.getStrengths().size() < 1) {
            throw new IllegalArgumentException("모든 필수 항목을 작성해야 합니다.");
        }

        // 경력 사항 검증
        if (Boolean.TRUE.equals(request.getIsExperienced())) {
            if (request.getWorkExperienceList() == null || request.getWorkExperienceList().isEmpty()) {
                throw new IllegalArgumentException("경력자라면 최소 1개의 경력 사항을 입력해야 합니다.");
            }
        }
    }

    // 특정 Resume 조회
    public ResumeDto getResumeById(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        return convertToDto(resume);
    }

    // 특정 User의 모든 Resume 조회
    public List<ResumeDto> getResumesByUserId(int userId) {
        List<Resume> resumes = resumeRepository.findByMember_MemberId(userId);
        return resumes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Resume -> ResumeDto 변환
    private ResumeDto convertToDto(Resume resume) {
        ResumeDto resumeDto = new ResumeDto();

        // 기본 필드 설정
        resumeDto.setId(resume.getId());
        resumeDto.setTitle(resume.getTitle());
        resumeDto.setProfileImage(resume.getProfileImage());
        resumeDto.setIsExperienced(resume.getIsExperienced());
        resumeDto.setPostcode(resume.getPostcode());
        resumeDto.setRoadAddress(resume.getRoadAddress());
        resumeDto.setDetailAddress(resume.getDetailAddress());

        resumeDto.setGrowthProcess(resume.getGrowthProcess());
        resumeDto.setPersonality(resume.getPersonality());
        resumeDto.setReason(resume.getReason());
        resumeDto.setJob(resume.getJob());
        resumeDto.setSpecialNote(resume.getSpecialNote());
        resumeDto.setStrengths(resume.getStrengths());

        // 학력 정보 변환
        List<AcademicInfoDto> academicInfoDtos = resume.getAcademicInfoList().stream()
                .map(info -> new AcademicInfoDto(info.getSchoolName(), info.getType(), info.getStartDate(), info.getEndDate(), info.getGraduationStatus()))
                .collect(Collectors.toList());
        resumeDto.setAcademicInfoList(academicInfoDtos);

        // 경력 정보 변환
        List<WorkExperienceDto> workExperienceDtos = resume.getWorkExperienceList().stream()
                .map(exp -> new WorkExperienceDto(exp.getCompanyName(), exp.getStartDate(), exp.getEndDate(), exp.getResponsibilities()))
                .collect(Collectors.toList());
        resumeDto.setWorkExperienceList(workExperienceDtos);

        // 자격증 정보 변환
        List<CertificateDto> certifications = resume.getCertifications().stream()
                .map(cert -> new CertificateDto(cert.getName(), cert.getDate()))
                .collect(Collectors.toList());
        resumeDto.setCertifications(certifications);

        // Member 정보 변환
        if (resume.getMember() != null) {
            resumeDto.setName(resume.getMember().getName());
            resumeDto.setEmail(resume.getMember().getEmail());
            resumeDto.setPhone_Number(resume.getMember().getPhone_number());
            resumeDto.setGender(resume.getMember().getGender());
        }

        // 총 경력 기간 설정
        String totalExperience = workExperienceService.calculateTotalExperience((resume.getId()));
        resumeDto.setTotalExperience(totalExperience);

        return resumeDto;
    }

    @Transactional
    public ResumeDto updateResume(Integer resumeId, ResumeRequestDto request) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        // 필드 업데이트
        resume.setTitle(request.getTitle());
        resume.setPostcode(request.getPostcode());
        resume.setRoadAddress(request.getRoadAddress());
        resume.setDetailAddress(request.getDetailAddress());
        resume.setStrengths(request.getStrengths());

        if (Boolean.TRUE.equals(request.getIsExperienced())) {
            // 기존 경력 삭제 및 새로운 경력 추가
            resume.getWorkExperienceList().clear();
            List<WorkExperience> workExperiences = request.getWorkExperienceList().stream()
                    .map(dto -> convertToWorkExperience(dto, resume))
                    .collect(Collectors.toList());
            resume.setWorkExperienceList(workExperiences);
        }

        if (request.getAcademicInfoList() != null) {
            // 기존 학력 삭제 및 새로운 학력 추가
            resume.getAcademicInfoList().clear();
            List<AcademicInfo> academicInfos = request.getAcademicInfoList().stream()
                    .map(dto -> convertToAcademicInfo(dto, resume))
                    .collect(Collectors.toList());
            resume.setAcademicInfoList(academicInfos);
        }

        // 저장 후 DTO 변환하여 반환
        return convertToDto(resume);
    }

    // 추가 자기소개서 항목 작성하기
    public ResumeDto addAdditionalFields(Integer resumeId, ResumeDto resumeDto) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // 추가 항목 업데이트
        if (resumeDto.getGrowthProcess() != null) resume.setGrowthProcess(resumeDto.getGrowthProcess());
        if (resumeDto.getPersonality() != null) resume.setPersonality(resumeDto.getPersonality());
        if (resumeDto.getReason() != null) resume.setReason(resumeDto.getReason());
        if (resumeDto.getJob() != null) resume.setJob(resumeDto.getJob());
        if (resumeDto.getSpecialNote() != null) resume.setSpecialNote(resumeDto.getSpecialNote());

        resumeRepository.save(resume);

        return convertToDto(resume);
    }

    // 프로필 이미지 업로드

    @Transactional
    public String saveProfileImage(Integer userId, MultipartFile file) throws IOException {
        // 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 업로드 경로가 존재하지 않으면 생성
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Upload directory created at: " + uploadPath); // 디렉토리 생성 여부 확인
        }

        // 저장할 파일 이름 설정
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadPath + File.separator + fileName; // OS에 따라 경로 구분자 처리
        System.out.println("File will be saved at: " + filePath); // 저장될 파일 경로 확인

        // 파일 저장
        File dest = new File(filePath);
        file.transferTo(dest);
        System.out.println("File transferred to: " + dest.getAbsolutePath()); // 파일 전송 확인

        // 해당 회원의 이력서 정보 가져오기
        Resume resume = resumeRepository.findByMember_MemberId(userId)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 이력서가 없습니다."));

        // 프로필 이미지 경로 설정 및 저장
        resume.setProfileImage("/uploads/" + fileName); // DB에 저장될 경로는 서버 기준 경로로 설정
        resumeRepository.save(resume);
        System.out.println("Resume Retrieved: " + resume); // 이력서 조회 결과 확인

        return "/uploads/" + fileName;
    }

    @Transactional
    public void deleteResume(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        // 이력서 삭제
        resumeRepository.delete(resume);
    }

    @Transactional
    public void addCertificates(Integer resumeId, List<CertificateDto> certificateDtos) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        List<Certification> certifications = certificateDtos.stream()
                .map(dto -> new Certification(dto.getName(), dto.getDate(), resume))
                .collect(Collectors.toList());

        // 자격증을 추가하여 누적 저장
        certificateRepository.saveAll(certifications);
    }

    // 특정 회원의 최근 작성 일자 바탕으로 조회
    public ResumeDto getLatestResumeByMemberId(Integer memberId) {
        List<Resume> resumes = resumeRepository.findByMember_MemberId(memberId);
        if (resumes.isEmpty()) {
            throw new IllegalArgumentException("No resumes found for Member ID: " + memberId);
        }
        Resume latestResume = resumes.get(0); // 최신 이력서를 찾는 로직 (필요 시 정렬 추가)
        return convertToDto(latestResume);
    }

    public Map<String, Object> getPersonalInfo(Integer memberId) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        // 해당 회원의 이력서 정보 가져오기
        Resume resume = resumeRepository.findByMember_MemberId(memberId)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resume not found for Member ID: " + memberId));

        // 정보를 맵 형태로 반환
        Map<String, Object> personalInfo = new HashMap<>();
        personalInfo.put("name", member.getName());
        personalInfo.put("email", member.getEmail());
        personalInfo.put("phone_number", member.getPhone_number());
        personalInfo.put("gender", member.getGender());
        personalInfo.put("profileImage", resume.getProfileImage());
        // personalInfo.put("birth_date", member.getBirth_date());
        personalInfo.put("address", resume.getRoadAddress() + " " + resume.getDetailAddress());

        return personalInfo;
    }

    // 새로운 경력 정보 추가 시 총 경력 계산 및 저장
    @Transactional
    public void addWorkExperience(WorkExperienceDto dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        WorkExperience experience = new WorkExperience();
        experience.setCompanyName(dto.getCompanyName());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());
        experience.setResponsibilities(dto.getResponsibilities());
        experience.setResume(resume);

        // 경력 기간 계산
        int months = experience.calculateDurationInMonths();

        // 총 경력 누적 계산
        String previousTotal = resume.getTotalExperience();
        int previousMonths = convertExperienceToMonths(previousTotal);
        int newTotalMonths = previousMonths + months;

        // 누적된 총 경력 기간 업데이트
        String totalExperience = convertMonthsToExperience(newTotalMonths);
        resume.setTotalExperience(totalExperience);

        // 저장
        workExperienceRepository.save(experience);
        resumeRepository.save(resume);
    }

    // 문자열 "00년 00개월" 형식을 월 수로 변환
    private int convertExperienceToMonths(String experience) {
        if (experience == null || experience.isEmpty()) return 0;
        String[] parts = experience.split(" ");
        int years = Integer.parseInt(parts[0].replace("년", ""));
        int months = Integer.parseInt(parts[1].replace("개월", ""));
        return years * 12 + months;
    }

    // 월 수를 "00년 00개월" 형식으로 변환
    private String convertMonthsToExperience(int totalMonths) {
        int years = totalMonths / 12;
        int months = totalMonths % 12;
        return years + "년 " + months + "개월";
    }
}

/*
    @Transactional
    public ResumeDto createResume(ResumeDto resumeDto, int memberId) { // 이력서 생성

        // 1. 멤버 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        // 2. 필수 항목 유효성 검사
        validateResumeFields(resumeDto);

        // 3. 이력서 생성 시 회원 정보 자동 포함
        resumeDto.setName(member.getName());
        resumeDto.setEmail(member.getEmail());
        resumeDto.setPhone_Number(member.getPhone_number());

        // 3. 이력서 생성 및 설정
        Resume resume = convertToEntity(resumeDto, member);
        resume.setMember(member);

        // 4. 학력 정보 설정
        List<AcademicInfo> academicInfos = resumeDto.getAcademicInfoList().stream()
                .map(info -> {
                    info.setResume(resume);
                    return info;
                }).collect(Collectors.toList());
        resume.setAcademicInfoList(academicInfos);

        // 4. 경력 사항 설정
        if (Boolean.TRUE.equals(resumeDto.getIsExperienced())) {
            List<WorkExperience> workExperiences = resumeDto.getWorkExperienceList().stream()
                    .map(exp -> {
                        exp.setResume(resume);
                        return exp;
                    }).collect(Collectors.toList());
            resume.setWorkExperienceList(workExperiences);
        } else {
            resume.setWorkExperienceList(Collections.emptyList());
        }

        // 6. 이력서 저장 (연관된 엔티티들도 함께 저장됨)
        Resume savedResume = resumeRepository.save(resume);
        academicInfoRepository.saveAll(academicInfos);

        // 7. 저장된 이력서를 DTO로 변환하여 반환
        return convertToDto(savedResume);
    }

    // 유효성 검사
    private void validateResumeFields(ResumeDto resumeDto) {
        if (resumeDto.getTitle() == null || resumeDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("이력서 제목은 필수 항목입니다.");
        }
        if (resumeDto.getPostcode() == null || resumeDto.getPostcode().isEmpty()) {
            throw new IllegalArgumentException("우편번호는 필수 항목입니다.");
        }
        if (resumeDto.getRoadAddress() == null || resumeDto.getRoadAddress().isEmpty()) {
            throw new IllegalArgumentException("도로명 주소는 필수 항목입니다.");
        }
        if (resumeDto.getDetailAddress() == null || resumeDto.getDetailAddress().isEmpty()) {
            throw new IllegalArgumentException("상세 주소는 필수 항목입니다.");
        }
        if (resumeDto.getAcademicInfoList() == null || resumeDto.getAcademicInfoList().isEmpty()) {
            throw new IllegalArgumentException("학력 정보는 최소 하나 이상 입력해야 합니다.");
        }
        if (resumeDto.getStrengths() == null || resumeDto.getStrengths().isEmpty()) {
            throw new IllegalArgumentException("강점은 최소 하나 이상 입력해야 합니다.");
        }
        if (resumeDto.getWorkExperienceList() == null || resumeDto.getWorkExperienceList().isEmpty()) {
            throw new IllegalArgumentException("경력 사항은 최소 하나 이상 입력해야 합니다.");
        }
    }

    // 추가 항목 생성 및 업데이트
    public ResumeDto addAdditionalFields(Long resumeId, ResumeDto resumeDto) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // 추가 항목 업데이트
        if (resumeDto.getGrowthProcess() != null) resume.setGrowthProcess(resumeDto.getGrowthProcess());
        if (resumeDto.getPersonality() != null) resume.setPersonality(resumeDto.getPersonality());
        if (resumeDto.getReason() != null) resume.setReason(resumeDto.getReason());
        if (resumeDto.getJob() != null) resume.setJob(resumeDto.getJob());
        if (resumeDto.getSpecialNote() != null) resume.setSpecialNote(resumeDto.getSpecialNote());

        resumeRepository.save(resume);

        return convertToDto(resume);
    }


    // 특정 회원의, 특정 이력서 조회 (resumeId로 조회)
    @Transactional
    public ResumeDto getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID"));

        // 2. Service를 통해 해당 이력서의 경력 목록을 가져옵니다.
        List<WorkExperience> experiences = workExperienceService.getWorkExperiences(resumeId);

        // 3. Service를 통해 총 경력 기간을 계산합니다.
        String totalExperience = workExperienceService.calculateTotalExperience(resumeId);

        // 4. 이력서를 DTO로 변환합니다.
        ResumeDto resumeDto = convertToDto(resume);

        // 5. 변환된 DTO에 경력 목록과 총 경력 기간을 추가합니다.
        resumeDto.setWorkExperienceList(experiences);
        resumeDto.setTotalExperience(totalExperience);

        // Entity를 Dto로 변환하여 반환
        return resumeDto;
    }

    public List<ResumeDto> getAllResumesByMemberId(Integer memberId) {
        List<Resume> resumes = resumeRepository.findByMember_MemberId(memberId);
        return resumes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 특정 회원의 모든 이력서 조회
    public List<ResumeDto> getResumesByMemberId(Integer memberId) {
        List<Resume> resumes = resumeRepository.findByMember_MemberId(memberId);

        return resumes.stream().map(resume -> {
            ResumeDto resumeDto = convertToDto(resume);

            // 총 경력 기간을 계산하여 DTO에 설정
            String totalExperience = workExperienceService.calculateTotalExperience(Long.valueOf(resume.getId()));
            resumeDto.setTotalExperience(totalExperience);

            return resumeDto;
        }).collect(Collectors.toList());


    }

    // 특정 회원의 최신 이력서 조회
    public ResumeDto getLatestResumeByMemberId(Integer memberId) {
        List<Resume> resumes = resumeRepository.findByMember_MemberId(memberId);
        if (resumes.isEmpty()) {
            throw new IllegalArgumentException("No resumes found for Member ID: " + memberId);
        }
        Resume latestResume = resumes.get(0); // 최신 이력서를 찾는 로직 (필요 시 정렬 추가)
        return convertToDto(latestResume);
    }


    // 사용자의 개인 정보와 이력서 정보를 가져오는 메서드
    // 이력서 조회 (제목, 사진, 개인 정보)
    public Resume getResumeWithMemberInfo(Integer memberId) {
        return resumeRepository.findByMember_MemberId(memberId)
                .stream().findFirst()
                .orElse(null);
    }

    // 사용자의 Member 정보를 가져오는 메서드 추가
    public Member getMemberInfo(Integer memberId) {
        return memberRepository.findById(memberId)
                .orElse(null);
    }

    // 주소 저장 - 2번 항목
    public boolean saveAddress(Integer memberId, Map<String, String> addressData) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Resume resume = resumeRepository.findByMember_MemberId(memberId)
                .stream().findFirst().orElse(new Resume());

        resume.setPostcode(addressData.get("postcode"));
        resume.setRoadAddress(addressData.get("roadAddress"));
        resume.setDetailAddress(addressData.get("detailAddress"));
        resume.setMember(member);

        resumeRepository.save(resume);
        return true;
    }

    // 사용자 정보 및 이력서 정보 가져오기
    public Map<String, Object> getPersonalInfo(Integer memberId) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        // 해당 회원의 이력서 정보 가져오기
        Resume resume = resumeRepository.findByMember_MemberId(memberId)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resume not found for Member ID: " + memberId));

        // 정보를 맵 형태로 반환
        Map<String, Object> personalInfo = new HashMap<>();
        personalInfo.put("name", member.getName());
        personalInfo.put("email", member.getEmail());
        personalInfo.put("phone_number", member.getPhone_number());
        personalInfo.put("gender", member.getGender());
        personalInfo.put("profileImage", resume.getProfileImage());
        // personalInfo.put("birth_date", member.getBirth_date());
        personalInfo.put("address", resume.getRoadAddress() + " " + resume.getDetailAddress());

        return personalInfo;
    }

    // Entity -> Dto 변환
    private ResumeDto convertToDto(Resume resume) {
        ResumeDto resumeDto = new ResumeDto();

        resumeDto.setId(resume.getId());
        resumeDto.setTitle(resume.getTitle());
        resumeDto.setProfileImage(resume.getProfileImage());
        resumeDto.setIsExperienced(resume.getIsExperienced());
        resumeDto.setPostcode(resume.getPostcode());
        resumeDto.setRoadAddress(resume.getRoadAddress());
        resumeDto.setDetailAddress(resume.getDetailAddress());

        resumeDto.setGrowthProcess(resume.getGrowthProcess());
        resumeDto.setPersonality(resume.getPersonality());
        resumeDto.setReason(resume.getReason());
        resumeDto.setJob(resume.getJob());
        resumeDto.setSpecialNote(resume.getSpecialNote());

        resumeDto.setStrengths(resume.getStrengths()); // 강점
        resumeDto.setAcademicInfoList(resume.getAcademicInfoList()); // 학력 정보
        resumeDto.setWorkExperienceList(resume.getWorkExperienceList()); // 경력 사항

        // 자격증 정보 변환하여 추가
        List<CertificateDto> certifications = resume.getCertifications().stream()
                .map(cert -> new CertificateDto(cert.getName(), cert.getDate()))
                .collect(Collectors.toList());
        resumeDto.setCertifications(certifications); // 자격증 정보 설정

        // 사용자 정보 추가 (Member 엔티티에서 가져옴)
        if (resume.getMember() != null) {
            resumeDto.setName(resume.getMember().getName());
            resumeDto.setEmail(resume.getMember().getEmail());
            resumeDto.setPhone_Number(resume.getMember().getPhone_number());
            resumeDto.setGender(resume.getMember().getGender());
        } else {
            // Member가 null일 경우 기본 값을 설정
            resumeDto.setName("");
            resumeDto.setEmail("");
            resumeDto.setPhone_Number("");
            resumeDto.setGender("");
        }

        // 총 경력 기간을 추가로 설정
        String totalExperience = workExperienceService.calculateTotalExperience(Long.valueOf(resume.getId()));
        resumeDto.setTotalExperience(totalExperience);

        return resumeDto;
    }

    // Dto -> Entity 변환
    private Resume convertToEntity(ResumeDto resumeDto, Member member) {
        Resume resume = new Resume();
        resume.setTitle(resumeDto.getTitle());
        resume.setProfileImage(resumeDto.getProfileImage());
        resume.setIsExperienced(resumeDto.getIsExperienced());
        resume.setPostcode(resumeDto.getPostcode());
        resume.setRoadAddress(resumeDto.getRoadAddress());
        resume.setDetailAddress(resumeDto.getDetailAddress());

        resume.setGrowthProcess(resumeDto.getGrowthProcess());
        resume.setPersonality(resumeDto.getPersonality());
        resume.setReason(resumeDto.getReason());
        resume.setJob(resumeDto.getJob());
        resume.setSpecialNote(resumeDto.getSpecialNote());

        resume.setStrengths(resumeDto.getStrengths());

        if (resumeDto.getAcademicInfoList() != null) {
            resume.setAcademicInfoList(resumeDto.getAcademicInfoList().stream()
                    .map(info -> {
                        info.setResume(resume);
                        return info;
                    }).collect(Collectors.toList()));
        }

        if (resumeDto.getWorkExperienceList() != null) {
            resume.setWorkExperienceList(resumeDto.getWorkExperienceList().stream()
                    .map(experience -> {
                        experience.setResume(resume);
                        return experience;
                    }).collect(Collectors.toList()));
        }

        // 자격증 정보 변환 및 설정 (Dto -> Entity)
        if (resumeDto.getCertifications() != null) {
            List<Certification> certifications = resumeDto.getCertifications().stream()
                    .map(certDto -> new Certification(certDto.getName(), certDto.getDate(), resume))
                    .collect(Collectors.toList());
            resume.setCertifications(certifications);
        }


        // 사용자 정보 설정 (Member 설정)
        if (member != null) {
            resume.setMember(member);
        }

        return resume;
    }

    // 제목과 해당 사용자의 주소 정보 조회 가능

    @Transactional
    public void saveTitle(String title, Integer memberId, Long resumeId) {
        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 회원의 이력서 가져오기 (없으면 새로 생성)
        Resume resume = resumeRepository.findByMember_MemberId(memberId)
                .stream().findFirst().orElse(new Resume());

        // 이력서가 해당 사용자와 연결되어 있는지 확인
        if (!Integer.valueOf(resume.getMember().getMemberId()).equals(memberId)) {
            throw new IllegalArgumentException("이력서가 해당 사용자와 일치하지 않습니다.");
        }

        // 제목 설정 및 저장
        resume.setMember(member);
        resume.setTitle(title);

        // 이력서 저장
        resumeRepository.save(resume);
    }

    // OCR로 제목을 인식하여 저장하는 메서드
    @Transactional
    public Resume saveTitleFromOcr(byte[] imageBytes, Integer memberId) {
        String ocrTitle = ocrApiService.extractTextFromImage(imageBytes); // OCR API 호출
        return saveTitle(ocrTitle, memberId);
    }

    // 강점 관련 메서드

    // 강점 업데이트 메서드
    public void updateStrengths(Integer resumeId, List<String> strengths) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        if (strengths.size() > 5) {
            throw new IllegalArgumentException("최대 5개의 강점만 선택할 수 있습니다.");
        }

        resume.setStrengths(strengths);
        resumeRepository.save(resume);
    }

    public void saveStrengths(Integer resumeId, List<String> strengths) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        resume.setStrengths(strengths);
        resumeRepository.save(resume);
    }

    public List<String> getStrengths(Integer resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        return resume.getStrengths();
    }

    // 새로운 경력 정보 추가 시 총 경력 계산 및 저장
    @Transactional
    public void addWorkExperience(WorkExperienceDto dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        WorkExperience experience = new WorkExperience();
        experience.setCompanyName(dto.getCompanyName());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());
        experience.setResponsibilities(dto.getResponsibilities());
        experience.setResume(resume);

        // 경력 기간 계산
        int months = experience.calculateDurationInMonths();

        // 총 경력 누적 계산
        String previousTotal = resume.getTotalExperience();
        int previousMonths = convertExperienceToMonths(previousTotal);
        int newTotalMonths = previousMonths + months;

        // 누적된 총 경력 기간 업데이트
        String totalExperience = convertMonthsToExperience(newTotalMonths);
        resume.setTotalExperience(totalExperience);

        // 저장
        workExperienceRepository.save(experience);
        resumeRepository.save(resume);
    }

    // 문자열 "00년 00개월" 형식을 월 수로 변환
    private int convertExperienceToMonths(String experience) {
        if (experience == null || experience.isEmpty()) return 0;
        String[] parts = experience.split(" ");
        int years = Integer.parseInt(parts[0].replace("년", ""));
        int months = Integer.parseInt(parts[1].replace("개월", ""));
        return years * 12 + months;
    }

    // 월 수를 "00년 00개월" 형식으로 변환
    private String convertMonthsToExperience(int totalMonths) {
        int years = totalMonths / 12;
        int months = totalMonths % 12;
        return years + "년 " + months + "개월";
    }

    @Transactional
    public void addCertificates(Integer resumeId, List<CertificateDto> certificateDtos) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        List<Certification> certifications = certificateDtos.stream()
                .map(dto -> new Certification(dto.getName(), dto.getDate(), resume))
                .collect(Collectors.toList());

        // 자격증을 추가하여 누적 저장
        certificateRepository.saveAll(certifications);
    }

    public List<CertificateDto> getCertificatesByResumeId(Long resumeId) {
        return certificateRepository.findByResume_Id(resumeId).stream()
                .map(cert -> new CertificateDto(cert.getName(), cert.getDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveCertificates(Integer resumeId, List<CertificateDto> certificates) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        List<Certification> certifications = certificates.stream()
                .map(certDto -> {
                    Certification cert = new Certification();
                    cert.setName(certDto.getName());
                    cert.setDate(certDto.getDate());
                    cert.setResume(resume);
                    return cert;
                })
                .collect(Collectors.toList());

        certificateRepository.saveAll(certifications);
    }
    }
 */
