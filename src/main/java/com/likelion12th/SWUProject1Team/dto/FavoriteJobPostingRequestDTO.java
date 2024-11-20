package com.likelion12th.SWUProject1Team.dto;

import lombok.Data;

@Data
public class FavoriteJobPostingRequestDTO {
	private Integer memberId;     // 사용자 ID
	private Integer jobPostingId; // 채용 공고 ID
}
