package com.likelion12th.SWUProject1Team.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OcrApiService {

    public String extractTextFromImage(byte[] imageBytes) {
        // OCR API 호출 로직 (예시)
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);
        String apiUrl = "http://your-ocr-api-endpoint"; // OCR API 엔드포인트 URL

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // 인식된 텍스트 반환
        } else {
            throw new RuntimeException("OCR API 호출 실패");
        }
    }

}
