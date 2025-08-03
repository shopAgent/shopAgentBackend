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
import org.spring.shopagent.setting.dto.AiConnectionTestResponseDTO;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestResponseDTO;
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


    public ApiResponseDto<String> getChat(ChatRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();




        ShopMapper shopMapper = shopMapperProvider.get();

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, "String");

    }
}
