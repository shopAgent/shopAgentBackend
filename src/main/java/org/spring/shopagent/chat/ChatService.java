package org.spring.shopagent.chat;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.spring.shopagent.chat.dto.ChatRequestDTO;
import org.spring.shopagent.common.AiClientCallService;
import org.spring.shopagent.common.DynamicDatabaseService;
import org.spring.shopagent.config.DbAutoInitializer;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.mapper.ShopMapper;
import org.spring.shopagent.mapper.provider.ShopMapperProvider;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.response.MsgType;
import org.spring.shopagent.response.ResponseUtils;
import org.spring.shopagent.setting.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final DynamicDatabaseService dynamicDatabaseService;

    private final ShopMapperProvider shopMapperProvider;

    private final H2Mapper h2Mapper;

    private final AiClientCallService aiClientCallService;


    public ApiResponseDto<List<java.util.Map<String, Object>>> getChat(ChatRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();

        ShopMapper shopMapper = shopMapperProvider.get();

        List<SearchRequestDTO> productConfigs = h2Mapper.selectSearchConfigByType("product");

        if (productConfigs.isEmpty()) {
            throw new CustomException(ErrorType.DATA_NOT_FOUND, "No product search config found");
        }

        String prompt = buildSqlPrompt(productConfigs, dto.getMessage());
        String sqlQuery = aiClientCallService.callAiClient(prompt);
        
        sqlQuery = extractSqlFromResponse(sqlQuery);

        List<java.util.Map<String, Object>> queryResult = shopMapper.executeDynamicQuery(sqlQuery);

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, queryResult);

    }

    private String buildSqlPrompt(List<SearchRequestDTO> configs, String userMessage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on the following database schema and user request, generate ONLY a SQL query (no explanations):\n\n");
        
        prompt.append("Available tables and their descriptions:\n");
        for (SearchRequestDTO config : configs) {
            prompt.append("- Table: ").append(config.getTableName()).append("\n");
            prompt.append("  Tool: ").append(config.getToolName()).append("\n");
            prompt.append("  Description: ").append(config.getDescription()).append("\n");
            prompt.append("  Product Name Column: ").append(config.getProductName()).append("\n");
            prompt.append("  Product Image Column: ").append(config.getProductImage()).append("\n\n");
        }
        
        prompt.append("User Request: ").append(userMessage).append("\n\n");
        prompt.append("Generate a SQL SELECT query to fulfill this request. Return ONLY the SQL query without any markdown formatting or explanations.");
        
        return prompt.toString();
    }

    private String extractSqlFromResponse(String aiResponse) {
        String cleanedResponse = aiResponse.trim();
        
        if (cleanedResponse.startsWith("```sql")) {
            cleanedResponse = cleanedResponse.substring(6);
        } else if (cleanedResponse.startsWith("```")) {
            cleanedResponse = cleanedResponse.substring(3);
        }
        
        if (cleanedResponse.endsWith("```")) {
            cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
        }
        
        return cleanedResponse.trim();
    }
}
