package com.likelion12th.SWUProject1Team.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class FavoriteJobPostingDTO {
    private Integer favoriteJobPostingsId; // 관심 공고 ID
    private Integer jobPostingId;         // 채용 공고 ID
    private String jobPostingTitle;       // 채용 공고 제목
    private Integer memberId;             // 사용자 ID
    private String memberName;            // 사용자 이름
    private LocalDateTime scrapDate;      // 스크랩 날짜
    private String siteAlias;             // 사이트 별칭
}