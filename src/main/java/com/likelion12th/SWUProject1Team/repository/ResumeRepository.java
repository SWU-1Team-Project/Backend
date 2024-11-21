package com.likelion12th.SWUProject1Team.repository;

import com.likelion12th.SWUProject1Team.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findByMember_MemberId(Integer memberId);

    @Query("SELECT r FROM Resume r " +
            "LEFT JOIN FETCH r.workExperienceList " +
            "LEFT JOIN FETCH r.academicInfoList " +
            "WHERE r.id = :resumeId")
    Optional<Resume> findResumeWithAssociations(@Param("resumeId") Integer resumeId);

}