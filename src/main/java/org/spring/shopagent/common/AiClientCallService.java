package org.spring.shopagent.common;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiClientCallService {

    @Autowired
    private H2Mapper h2Mapper;

    public String callAiClient(String prompt) {

        String model = "gemini-2.5-flash";

        try {
            // H2 DB에서 API key 가져오기
            DatabaseInfoDTO dbInfo = h2Mapper.selectDatabaseInfo();
            if (dbInfo == null || dbInfo.getToken() == null || dbInfo.getToken().isEmpty()) {
                return "Error: API key not configured. Please set up your Gemini API key in database settings.";
            }

            String apiKey = dbInfo.getToken();

            Client client = Client.builder()
                    .apiKey(apiKey)
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            model,
                            prompt,
                            null);

            return response.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI client: " + e.getMessage();
        }

    }


}
