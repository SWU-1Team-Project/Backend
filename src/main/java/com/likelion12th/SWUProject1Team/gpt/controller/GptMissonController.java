package com.likelion12th.SWUProject1Team.gpt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion12th.SWUProject1Team.gpt.dto.GptReqDto;
import com.likelion12th.SWUProject1Team.gpt.dto.GptResDto;
import com.likelion12th.SWUProject1Team.gpt.service.GptMissonService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/gptmissions")
@RequiredArgsConstructor
public class GptMissonController {
	private final GptMissonService gptMissionService;

	@PostMapping("/correct_text")
	public ResponseEntity<GptResDto> correctText(@RequestBody GptReqDto request) {

		// 사용자 입력 텍스트 가져오기
		String userInput = request.getText();

		// 카테고리에 맞는 프롬프트 생성
		String prompt = createPromptForCorrection(userInput);

		try {
			// GPT API 호출
			String  correctedText  = gptMissionService.callGptApi(prompt);

			// 응답 생성
			GptResDto response = GptResDto.builder()
					.correctedText(correctedText)
					.build();

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
				.body(GptResDto.builder()
						.error("Failed to correct text")
						.build());
		}
	}

	// 프롬프트 생성 함수
	private String createPromptForCorrection(String userInput) {
		return String.format("다음 문장을 올바른 맞춤법으로 수정해줘:\n\n%s", userInput);
	}
}