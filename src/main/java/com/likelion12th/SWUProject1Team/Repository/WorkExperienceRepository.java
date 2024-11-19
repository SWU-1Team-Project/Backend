package com.likelion12th.SWUProject1Team.Repository;

import com.likelion12th.SWUProject1Team.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByResumeId(Long resumeId); // resumeId로 WorkExperience 찾기

    List<WorkExperience> findByResume_Id(Long resumeId);
}
