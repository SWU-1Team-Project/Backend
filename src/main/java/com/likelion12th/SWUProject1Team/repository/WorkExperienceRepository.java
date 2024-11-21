package com.likelion12th.SWUProject1Team.repository;

import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    List<WorkExperience> findByResumeId(Integer resumeId); // resumeId로 WorkExperience 찾기

    List<WorkExperience> findByResume_Id(Integer resumeId);
}
