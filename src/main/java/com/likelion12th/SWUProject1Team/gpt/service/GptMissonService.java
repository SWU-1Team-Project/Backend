package com.likelion12th.SWUProject1Team.gpt.service;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GptMissonService {

	private final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";
	@Value("${gpt.secret}")
	private String GPT_API_KEY;

	public String callGptApi(String prompt) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		// GPT API 요청 본문 생성
		JSONObject requestBody = new JSONObject();
		requestBody.put("model", "gpt-4o");
		JSONArray messages = new JSONArray();
		JSONObject message = new JSONObject();
		message.put("role", "user");
		message.put("content", prompt);
		messages.put(message);
		requestBody.put("messages", messages);

		// HTTP 헤더 설정 (API 키 포함)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(GPT_API_KEY);

		// HTTP 요청 생성
		HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		// GPT API 호출
		ResponseEntity<String> response = restTemplate.exchange(
			GPT_API_URL,
			HttpMethod.POST,
			entity,
			String.class
		);

		// 응답 본문에서 미션 추출 (예시로 간단한 JSON 처리)
		JSONObject jsonResponse = new JSONObject(response.getBody());
		return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
	}
	
	public static void main(String[] args) throws Exception {
		GptMissonService service = new GptMissonService();
		String res = service.callGptApi("다음 문장을 올바른 맞춤법으로 수정해줘");
		System.out.println(res);
	}
	
}