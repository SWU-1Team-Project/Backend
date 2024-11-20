package com.likelion12th.SWUProject1Team.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion12th.SWUProject1Team.dto.JobPostingDTO;
import com.likelion12th.SWUProject1Team.service.JobPostingService;

@RestController
@RequestMapping("/api/v1/jobPostings/crawler")
public class CrawlerController {

	private final JobPostingService jobPostingService;

	public CrawlerController(JobPostingService jobPostingService) {
		this.jobPostingService = jobPostingService;
	}

	// URL 기반 크롤링 API
	@PostMapping("/jobPostings")
	public ResponseEntity<?> crawlAndSaveJobPosting(@RequestBody String url) {
		try {
			// 크롤링 수행 및 DB 저장
			JobPostingDTO jobPostingDTO = jobPostingService.crawlAndSaveJobDetails(url);

			// 크롤링 결과 반환
			return ResponseEntity.ok(jobPostingDTO);
		} catch (IllegalArgumentException e) {
			// 크롤링 실패 응답
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			// 기타 실패 응답
			return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
		}
	}
}

