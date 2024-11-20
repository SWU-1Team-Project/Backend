package com.likelion12th.SWUProject1Team.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

import com.likelion12th.SWUProject1Team.dto.JobPostingDTO;
import com.likelion12th.SWUProject1Team.entity.JobPosting;
import com.likelion12th.SWUProject1Team.repository.JobPostingRepository;

@Service
public class JobPostingService {

	@Autowired
	private JobPostingRepository jobPostingRepository;

	// 메모 업데이트 메서드
	public boolean updateMemo(Integer jobPostingId, String memo) {
		Optional<JobPosting> optionalJobPosting = jobPostingRepository.findById(jobPostingId);
		if (optionalJobPosting.isPresent()) {
			JobPosting jobPosting = optionalJobPosting.get();
			jobPosting.setMemo(memo); // 메모 업데이트
			jobPostingRepository.save(jobPosting); // 저장
			return true;
		}
		return false; // JobPosting이 없을 경우 false 반환
	}

	// URL 기반 크롤링 및 데이터 저장
	public JobPostingDTO crawlAndSaveJobDetails(String url) {
		try {
			// Jsoup으로 HTML 페이지 가져오기
			Document doc = Jsoup.connect(url)
				.userAgent("Mozilla/5.0")
				.get();

			// 데이터 추출
			String companyName = getText(doc.selectFirst("p.prod_name span.name"));
			String jobTitle = getText(doc.selectFirst("h1.prod_title"));
			String hiringStatus = getText(doc.selectFirst("p.prod_name span.d_day"));
			String interestCount = getText(doc.selectFirst("ul.noti_list li.bg_02 em.emp em"));
			String location = getText(doc.selectFirst("p.current span"));
			String contactInfo = getText(doc.selectFirst("span.con.num"));

			// 데이터 유효성 검사
			if (companyName == null || jobTitle == null) {
				throw new IllegalArgumentException("Required fields are missing in the provided URL.");
			}

			// JobPosting 엔티티 생성
			JobPosting jobPosting = new JobPosting(
				companyName, jobTitle, hiringStatus, interestCount, location, contactInfo
			);

			// DB 저장
			jobPostingRepository.save(jobPosting);

			// DTO로 변환 후 반환
			return toDTO(jobPosting);
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to fetch data from the provided URL.");
		}
	}

	private String getText(Element element) {
		return element != null ? element.text().trim() : "N/A";
	}

	// 개별 JobPosting 조회 메서드
	public JobPostingDTO getJobPostingById(Integer jobPostingId) {
		Optional<JobPosting> jobPosting = jobPostingRepository.findById(jobPostingId);
		return jobPosting.map(this::toDTO).orElse(null);
	}

	// 개별 JobPosting 수정
	public JobPostingDTO updateJobPosting(Integer jobPostingId, JobPosting updatedJobPosting) {
		Optional<JobPosting> optionalJobPosting = jobPostingRepository.findById(jobPostingId);
		if (optionalJobPosting.isPresent()) {
			JobPosting existingJobPosting = optionalJobPosting.get();

			existingJobPosting.setCompanyName(updatedJobPosting.getCompanyName());
			existingJobPosting.setJobTitle(updatedJobPosting.getJobTitle());
			existingJobPosting.setHiringStatus(updatedJobPosting.getHiringStatus());
			existingJobPosting.setInterestCount(updatedJobPosting.getInterestCount());
			existingJobPosting.setLocation(updatedJobPosting.getLocation());
			existingJobPosting.setContactInfo(updatedJobPosting.getContactInfo());

			JobPosting savedJobPosting = jobPostingRepository.save(existingJobPosting);
			return toDTO(savedJobPosting);
		}
		return null;
	}

	// 개별 JobPosting 삭제
	public boolean deleteJobPosting(Integer jobPostingId) {
		if (jobPostingRepository.existsById(jobPostingId)) {
			jobPostingRepository.deleteById(jobPostingId);
			return true;
		}
		return false;
	}

	// Entity -> DTO 변환 메서드
	private JobPostingDTO toDTO(JobPosting jobPosting) {
		return new JobPostingDTO(
			jobPosting.getJobPostingId(),
			jobPosting.getCompanyName(),
			jobPosting.getJobTitle(),
			jobPosting.getHiringStatus(),
			jobPosting.getInterestCount(),
			jobPosting.getLocation(),
			jobPosting.getContactInfo()
		);
	}
}


