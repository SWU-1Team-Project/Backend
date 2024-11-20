package com.likelion12th.SWUProject1Team.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion12th.SWUProject1Team.dto.FavoriteJobPostingDTO;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.entity.FavoriteJobPosting;
import com.likelion12th.SWUProject1Team.entity.JobPosting;
import com.likelion12th.SWUProject1Team.repository.FavoriteJobPostingRepository;
import com.likelion12th.SWUProject1Team.repository.JobPostingRepository;
import com.likelion12th.SWUProject1Team.repository.MemberRepository;

@Service
public class FavoriteJobPostingService {

	private final FavoriteJobPostingRepository favoriteJobPostingRepository;
	private final MemberRepository memberRepository;
	private final JobPostingRepository jobPostingRepository;

	public FavoriteJobPostingService(FavoriteJobPostingRepository favoriteJobPostingRepository,
		MemberRepository memberRepository,
		JobPostingRepository jobPostingRepository) {
		this.favoriteJobPostingRepository = favoriteJobPostingRepository;
		this.memberRepository = memberRepository;
		this.jobPostingRepository = jobPostingRepository;
	}

	// 최근 2개의 관심 공고 가져오기
	public List<FavoriteJobPostingDTO> getRecentFavorites(Integer memberId) {
		Pageable pageable = PageRequest.of(0, 2);
		return favoriteJobPostingRepository.findRecentFavorites(memberId, pageable)
			.getContent()
			.stream()
			.map(f -> new FavoriteJobPostingDTO(
				f.getFavoriteJobPostingsId(),
				f.getJobPosting().getJobPostingId(),
				f.getJobPosting().getJobTitle(),
				f.getMember().getMemberId(),
				f.getMember().getName(),
				f.getScrapDate(),
				f.getSiteAlias()
			))
			.collect(Collectors.toList());
	}

	// 관심 공고 추가
	@Transactional
	public FavoriteJobPostingDTO addFavoriteJobPosting(Integer memberId, Integer jobPostingId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member with ID " + memberId + " not found"));
		JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
			.orElseThrow(() -> new IllegalArgumentException("JobPosting with ID " + jobPostingId + " not found"));

		FavoriteJobPosting favoriteJobPosting = new FavoriteJobPosting();
		favoriteJobPosting.setMember(member);
		favoriteJobPosting.setJobPosting(jobPosting);
		favoriteJobPosting.setScrapDate(LocalDateTime.now());

		FavoriteJobPosting savedFavoriteJobPosting = favoriteJobPostingRepository.save(favoriteJobPosting);

		return new FavoriteJobPostingDTO(
			savedFavoriteJobPosting.getFavoriteJobPostingsId(),
			savedFavoriteJobPosting.getJobPosting().getJobPostingId(),
			savedFavoriteJobPosting.getJobPosting().getJobTitle(),
			savedFavoriteJobPosting.getMember().getMemberId(),
			savedFavoriteJobPosting.getMember().getName(),
			savedFavoriteJobPosting.getScrapDate(),
			savedFavoriteJobPosting.getSiteAlias()
		);
	}

	// 관심 공고 목록 전체 조회
	public List<FavoriteJobPostingDTO> getFavoriteJobPostings(Integer memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("Member with ID " + memberId + " not found"));

		return favoriteJobPostingRepository.findAllByMemberOrderByScrapDateDesc(member)
			.stream()
			.map(f -> new FavoriteJobPostingDTO(
				f.getFavoriteJobPostingsId(),
				f.getJobPosting().getJobPostingId(),
				f.getJobPosting().getJobTitle(),
				f.getMember().getMemberId(),
				f.getMember().getName(),
				f.getScrapDate(),
				f.getSiteAlias()
			))
			.collect(Collectors.toList());
	}
}
