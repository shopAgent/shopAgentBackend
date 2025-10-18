package org.spring.shopagent.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiClientCallService {

    @Autowired
    private H2Mapper h2Mapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callAiClient(String prompt) {

        String model = "gemini-2.0-flash-exp";

        try {
            // H2 DB에서 API key 가져오기
            DatabaseInfoDTO dbInfo = h2Mapper.selectDatabaseInfo();
            if (dbInfo == null || dbInfo.getToken() == null || dbInfo.getToken().isEmpty()) {
                return "Error: API key not configured. Please set up your Gemini API key in database settings.";
            }

            String apiKey = dbInfo.getToken();

            // Google Gemini API 직접 호출
            String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model,
                apiKey
            );

            // 요청 body 생성
            String requestBody = String.format(
                "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                prompt.replace("\"", "\\\"").replace("\n", "\\n")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Gemini API Error: " + response.statusCode());
                System.err.println("Response: " + response.body());
                return "Error calling AI client: HTTP " + response.statusCode();
            }

            // JSON 응답 파싱
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            String text = jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return text;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI client: " + e.getMessage();
        }

    }


}
