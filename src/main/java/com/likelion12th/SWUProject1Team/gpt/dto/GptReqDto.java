package com.likelion12th.SWUProject1Team.gpt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GptReqDto {
	private String text; // 맞춤법 검사 대상 텍스트
}
