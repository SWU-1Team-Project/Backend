package com.likelion12th.SWUProject1Team.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion12th.SWUProject1Team.dto.JobPostingDTO;
import com.likelion12th.SWUProject1Team.entity.JobPosting;
import com.likelion12th.SWUProject1Team.service.JobPostingService;

@RestController
@RequestMapping("/api/v1/jobPostings")
public class JobPostingController {

	private final JobPostingService jobPostingService;

	public JobPostingController(JobPostingService jobPostingService) {
		this.jobPostingService = jobPostingService;
	}


	// 메모 업데이트 API
	@PatchMapping("/{posting_id}/memo")
	public ResponseEntity<Void> updateMemo(
		@PathVariable("posting_id") Integer jobPostingId,
		@RequestBody String memo) {
		boolean isUpdated = jobPostingService.updateMemo(jobPostingId, memo);
		if (isUpdated) {
			return ResponseEntity.ok().build(); // 성공 시 200 반환
		} else {
			return ResponseEntity.notFound().build(); // 실패 시 404 반환
		}
	}

	// 개별 JobPosting 조회 API
	@GetMapping("/{posting_id}")
	public ResponseEntity<JobPostingDTO> getJobPostingById(@PathVariable("posting_id") Integer jobPostingId) {
		JobPostingDTO jobPosting = jobPostingService.getJobPostingById(jobPostingId);
		if (jobPosting != null) {
			return ResponseEntity.ok(jobPosting);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 개별 JobPosting 수정 API
	@PutMapping("/{posting_id}")
	public ResponseEntity<JobPostingDTO> updateJobPosting(
		@PathVariable("posting_id") Integer jobPostingId,
		@RequestBody JobPosting updatedJobPosting) {
		JobPostingDTO jobPosting = jobPostingService.updateJobPosting(jobPostingId, updatedJobPosting);
		if (jobPosting != null) {
			return ResponseEntity.ok(jobPosting);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 개별 JobPosting 삭제 API
	@DeleteMapping("/{posting_id}")
	public ResponseEntity<Void> deleteJobPosting(@PathVariable("posting_id") Integer jobPostingId) {
		boolean isDeleted = jobPostingService.deleteJobPosting(jobPostingId);
		if (isDeleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}




