package com.likelion12th.SWUProject1Team.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.likelion12th.SWUProject1Team.dto.FavoriteJobPostingDTO;
import com.likelion12th.SWUProject1Team.dto.FavoriteJobPostingRequestDTO;
import com.likelion12th.SWUProject1Team.service.FavoriteJobPostingService;

@RestController
@RequestMapping("/api/v1/favoriteJobPostings")
public class FavoriteJobPostingController {

	private final FavoriteJobPostingService favoriteJobPostingService;

	public FavoriteJobPostingController(FavoriteJobPostingService favoriteJobPostingService) {
		this.favoriteJobPostingService = favoriteJobPostingService;
	}

	// 최근 2개의 관심 채용 공고 조회
	@GetMapping("/recent")
	public ResponseEntity<List<FavoriteJobPostingDTO>> getRecentFavorites(@RequestParam Integer memberId) {
		List<FavoriteJobPostingDTO> recentFavorites = favoriteJobPostingService.getRecentFavorites(memberId);
		return ResponseEntity.ok(recentFavorites);
	}

	// 관심 채용 공고 추가
	@PostMapping("/add")
	public ResponseEntity<FavoriteJobPostingDTO> addFavoriteJobPosting(
		@RequestBody FavoriteJobPostingRequestDTO request) {
		FavoriteJobPostingDTO favoriteJobPosting = favoriteJobPostingService.addFavoriteJobPosting(
			request.getMemberId(), request.getJobPostingId());
		return ResponseEntity.ok(favoriteJobPosting);
	}

	// 관심 채용 공고 전체목록 조회
	@GetMapping("/list")
	public ResponseEntity<List<FavoriteJobPostingDTO>> getFavoriteJobPostings(@RequestParam Integer memberId) {
		List<FavoriteJobPostingDTO> favoriteJobPostings = favoriteJobPostingService.getFavoriteJobPostings(memberId);
		return ResponseEntity.ok(favoriteJobPostings);
	}
}

