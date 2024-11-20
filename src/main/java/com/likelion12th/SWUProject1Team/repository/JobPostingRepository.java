package com.likelion12th.SWUProject1Team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion12th.SWUProject1Team.entity.JobPosting;

public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {
}