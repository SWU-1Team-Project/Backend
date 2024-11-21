package com.likelion12th.SWUProject1Team.repository;

import com.likelion12th.SWUProject1Team.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findByMember_MemberId(Integer memberId);
}
