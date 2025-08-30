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


    public ApiResponseDto<Object> getChat(ChatRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();

        // 1. 메시지 유형 판단
        String messageType = determineMessageType(dto.getMessage());
        
        if ("PRODUCT_SEARCH".equals(messageType)) {
            // 상품 검색 요청인 경우
            return handleProductSearch(dto);
        } else {
            // 일반 대화인 경우
            return handleGeneralChat(dto);
        }

    }

    private String determineMessageType(String message) {
        String prompt = "다음 사용자 메시지를 분석해서 상품 검색 요청인지 일반 대화인지 판단해주세요. " +
                "상품 검색, 제품 찾기, 쇼핑 관련, 주문 조회, 재고 확인 등의 내용이면 'PRODUCT_SEARCH', " +
                "그 외 인사, 일반 질문, 대화는 'GENERAL_CHAT'로만 답변해주세요.\n\n" +
                "사용자 메시지: " + message;
        
        String aiResponse = aiClientCallService.callAiClient(prompt);
        return aiResponse.trim().toUpperCase().contains("PRODUCT_SEARCH") ? "PRODUCT_SEARCH" : "GENERAL_CHAT";
    }

    private ApiResponseDto<Object> handleProductSearch(ChatRequestDTO dto) {
        ShopMapper shopMapper = shopMapperProvider.get();

        List<SearchRequestDTO> productConfigs = h2Mapper.selectSearchConfigByType("product");

        if (productConfigs.isEmpty()) {
            throw new CustomException(ErrorType.DATA_NOT_FOUND, "상품 검색 설정이 없습니다.");
        }

        String prompt = buildSqlPrompt(productConfigs, dto.getMessage());
        String sqlQuery = aiClientCallService.callAiClient(prompt);
        
        sqlQuery = extractSqlFromResponse(sqlQuery);

        try {
            List<java.util.Map<String, Object>> queryResult = shopMapper.executeDynamicQuery(sqlQuery);
            return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, queryResult);
        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR, 
                "SQL 쿼리 실행 중 오류가 발생했습니다. DB 연결 정보를 확인해주세요. " +
                "생성된 쿼리: " + sqlQuery + " | 오류: " + e.getMessage());
        }
    }

    private ApiResponseDto<Object> handleGeneralChat(ChatRequestDTO dto) {
        String prompt = "You are a friendly and helpful assistant. Please respond to the user's message in a natural and conversational way. " +
                "Detect the language of the user's message and respond in the same language. " +
                "If the user writes in Korean, respond in Korean. If they write in English, respond in English.\n\n" +
                "User message: " + dto.getMessage();
        
        String aiResponse = aiClientCallService.callAiClient(prompt);
        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, aiResponse);
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
