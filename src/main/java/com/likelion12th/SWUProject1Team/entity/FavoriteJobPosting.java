package com.likelion12th.SWUProject1Team.entity;

import java.time.LocalDateTime;

import com.likelion12th.SWUProject1Team.entity.JobPosting;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FavoriteJobPosting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteJobPostingsId; // 관심 공고 ID

	@ManyToOne
	@JoinColumn(name = "jobPosting_id", nullable = false)
	private JobPosting jobPosting; // 채용 공고 ID

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member; // 사용자 ID

	private LocalDateTime scrapDate; // 스크랩 날짜

	private String siteAlias; //사이트 별칭 저장
}

