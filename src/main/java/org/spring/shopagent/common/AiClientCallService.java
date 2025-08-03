package org.spring.shopagent.common;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class AiClientCallService {

    public String callAiClient(String prompt) {

        String model = "gemini-2.5-flash";

        try {

            Client client = Client.builder()
                    .apiKey("AIzaSyCDO3AwF3obCO3O1E1ixjyctZmhKxIxWCw")
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
