package com.likelion12th.SWUProject1Team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "jobposting")
public class JobPosting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer jobPostingId; // 채용 공고 ID

	private String companyName;
	private String jobTitle;
	private String hiringStatus; // 채용 상태
	private String interestCount;
	private String location;
	private String contactInfo;

	@Column(columnDefinition = "TEXT")
	private String memo; // 메모 필드 추가

	// 생성자
	public JobPosting(String companyName, String jobTitle, String hiringStatus, String interestCount, String location, String contactInfo) {
		this.companyName = companyName;
		this.jobTitle = jobTitle;
		this.hiringStatus = hiringStatus;
		this.interestCount = interestCount;
		this.location = location;
		this.contactInfo = contactInfo;
		this.memo = memo;
	}
}
