package com.likelion12th.SWUProject1Team.repository;

import com.likelion12th.SWUProject1Team.entity.AcademicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicInfoRepository extends JpaRepository <AcademicInfo, Long> {

    // 새로운 메서드 선언: 특정 이력서 ID와 학교명이 존재하는지 확인
    boolean existsByResume_IdAndSchoolName(Integer resumeId, String schoolName);

    List<AcademicInfo> findByResume_Id(Integer resumeId);

    // 특정 학교 이름과 타입을 기준으로 검색
    List<AcademicInfo> findBySchoolNameContainingAndType(String schoolName, String type);

    // 학교 이름에 검색 키워드가 포함된 항목을 찾는 메서드
    List<AcademicInfo> findBySchoolNameContaining(String searchKeyword);

    // 학교 이름으로 존재 여부 확인 메서드
    boolean existsBySchoolName(String schoolName);

    List<AcademicInfo> findByResume_Id(Long resumeId);
    // 모든 학교 이름을 가져오는 쿼리 메서드 추가 (중복 체크 최적화에 활용)
    @Query("SELECT a.schoolName FROM AcademicInfo a")
    List<String> findAllSchoolNames();

    // 특정 Resume ID와 data_source가 'user'인 데이터만 조회
    List<AcademicInfo> findByResume_IdAndDataSource(Integer resumeId, String dataSource);
}
