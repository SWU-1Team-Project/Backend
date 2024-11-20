package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.dto.SchoolInfo;
import com.likelion12th.SWUProject1Team.entity.AcademicInfo;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.repository.AcademicInfoRepository;
import com.likelion12th.SWUProject1Team.repository.ResumeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AcademicInfoService {

    @Value("${career.api.url}")
    private String apiUrl;

    @Value("${career.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AcademicInfoRepository academicInfoRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    // 초기화 시 외부 API에서 데이터를 가져와 저장
    @PostConstruct
    public void initialize() {
        fetchAndSaveAllSchools();
    }

    // 모든 학교 데이터를 외부 API에서 가져와 저장
    public void fetchAndSaveAllSchools() {
        List<SchoolInfo> universities = getSchoolInfoData("univ_list");
        saveAcademicInfoList(universities, "university", "api");

        List<SchoolInfo> highSchools = getSchoolInfoData("high_list");
        saveAcademicInfoList(highSchools, "highschool", "api");
    }

    // 외부 API에서 학교 데이터 가져오기
    public List<SchoolInfo> getSchoolInfoData(String gubun) {
        List<SchoolInfo> schoolInfoList = new ArrayList<>();
        int currentPage = 1;
        boolean hasMoreData = true;

        while (hasMoreData) {
            try {
                URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("apiKey", apiKey)
                        .queryParam("svcType", "api")
                        .queryParam("svcCode", "SCHOOL")
                        .queryParam("contentType", "json")
                        .queryParam("gubun", gubun)
                        .queryParam("thisPage", currentPage)
                        .queryParam("perPage", "100")
                        .build(true)
                        .toUri();

                String jsonString = restTemplate.getForObject(uri, String.class);
                List<SchoolInfo> pageData = parseResponse(jsonString);
                schoolInfoList.addAll(pageData);
                hasMoreData = !pageData.isEmpty();
                currentPage++;
            } catch (Exception e) {
                e.printStackTrace();
                hasMoreData = false;
            }
        }
        return schoolInfoList;
    }

    // JSON 응답을 SchoolInfo 리스트로 파싱
    private List<SchoolInfo> parseResponse(String response) {
        List<SchoolInfo> schoolInfoList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode contentArray = root.path("dataSearch").path("content");

            for (JsonNode node : contentArray) {
                SchoolInfo schoolInfo = objectMapper.treeToValue(node, SchoolInfo.class);
                schoolInfoList.add(schoolInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return schoolInfoList;
    }

    public void saveAcademicInfoList(List<SchoolInfo> schoolInfoList, String type, String dataSource) {
        // 1. 이미 저장된 학교 목록을 미리 가져와서 메모리에 저장
        List<String> existingSchoolNames = academicInfoRepository.findAllSchoolNames();

        // 2. 중복되지 않는 학교 정보만 필터링
        List<AcademicInfo> newAcademicInfos = schoolInfoList.stream()
                .filter(schoolInfo -> !existingSchoolNames.contains(schoolInfo.getSchoolName()))
                .map(schoolInfo -> {
                    AcademicInfo academicInfo = new AcademicInfo();
                    academicInfo.setSchoolName(schoolInfo.getSchoolName());
                    academicInfo.setSchoolGubun(schoolInfo.getSchoolGubun());
                    academicInfo.setRegion(schoolInfo.getRegion());
                    academicInfo.setAddress(schoolInfo.getAddress());
                    academicInfo.setStartDate(schoolInfo.getStartDate());
                    academicInfo.setEndDate(schoolInfo.getEndDate());
                    academicInfo.setGraduationStatus(schoolInfo.getGraduationStatus());
                    academicInfo.setGraduateSchoolName(schoolInfo.getGraduateSchoolName());
                    academicInfo.setMajor(schoolInfo.getMajor());
                    academicInfo.setType(type);
                    academicInfo.setDataSource(dataSource);
                    return academicInfo;
                })
                .toList();

        // 3. 새로운 학교 정보만 한 번에 저장 (배치 저장)
        if (!newAcademicInfos.isEmpty()) {
            academicInfoRepository.saveAll(newAcademicInfos);
        }
    }

    public void saveAcademicInfo(SchoolInfo schoolInfo, String type, String dataSource) {
        AcademicInfo academicInfo = new AcademicInfo();
        academicInfo.setSchoolName(schoolInfo.getSchoolName());
        academicInfo.setSchoolGubun(schoolInfo.getSchoolGubun());
        academicInfo.setRegion(schoolInfo.getRegion());
        academicInfo.setAddress(schoolInfo.getAddress());
        academicInfo.setStartDate(schoolInfo.getStartDate());
        academicInfo.setEndDate(schoolInfo.getEndDate());
        academicInfo.setGraduationStatus(schoolInfo.getGraduationStatus());
        academicInfo.setGraduateSchoolName(schoolInfo.getGraduateSchoolName());
        academicInfo.setMajor(schoolInfo.getMajor());
        academicInfo.setType(type);
        academicInfo.setDataSource(dataSource);

        if (!academicInfoRepository.existsBySchoolName(academicInfo.getSchoolName())) {
            academicInfoRepository.save(academicInfo);
        }
    }

    // 단일 AcademicInfo 객체를 저장하는 메서드 (오버로드)
    public void saveAcademicInfo(Long resumeId, AcademicInfo academicInfo) {
        // 이력서 조회 및 검증
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        // 학력 정보에 이력서 설정
        academicInfo.setResume(resume);

        // 중복 방지 및 학력 정보 저장
        if (!academicInfoRepository.existsByResume_IdAndSchoolName(resumeId, academicInfo.getSchoolName())) {
            academicInfoRepository.save(academicInfo);
        }
    }

    // String을 LocalDate로 변환하는 메서드
    private LocalDate convertStringToDate(String date) {
        if (date == null || date.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    // 로컬 DB에서 학교 검색
    public List<AcademicInfo> searchSchoolFromLocal(String searchKeyword) {
        return academicInfoRepository.findBySchoolNameContaining(searchKeyword);
    }

    // 외부 API를 통해 특정 학교 검색
    public List<SchoolInfo> getSchoolInfo(String searchKeyword) {
        List<SchoolInfo> combinedResults = new ArrayList<>();
        combinedResults.addAll(searchSchoolsByType(searchKeyword, "univ_list"));
        combinedResults.addAll(searchSchoolsByType(searchKeyword, "high_list"));
        return combinedResults;
    }

    // 외부 API를 통해 학교 검색 (학교 유형별)
    private List<SchoolInfo> searchSchoolsByType(String searchKeyword, String gubun) {
        try {
            String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("apiKey", apiKey)
                    .queryParam("svcType", "api")
                    .queryParam("svcCode", "SCHOOL")
                    .queryParam("contentType", "json")
                    .queryParam("gubun", gubun)
                    .queryParam("thisPage", "1")
                    .queryParam("perPage", "10")
                    .queryParam("searchSchulNm", encodedKeyword)
                    .build(true)
                    .toUri();

            String jsonString = restTemplate.getForObject(uri, String.class);
            return parseResponse(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // 특정 Resume ID에 대해 data_source가 'user'인 학력 정보 조회 메서드
    public List<AcademicInfo> getUserAcademicInfoByResumeId(Long resumeId) {
        return academicInfoRepository.findByResume_IdAndDataSource(resumeId, "user");
    }

    // 특정 Resume ID로 학력 정보를 조회하는 메서드 추가
    public List<AcademicInfo> getAcademicInfoByResumeId(Long resumeId) {
        return academicInfoRepository.findByResume_Id(resumeId);
    }

}
