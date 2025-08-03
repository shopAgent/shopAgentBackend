package org.spring.shopagent.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.shopagent.chat.dto.ChatRequestDTO;
import org.spring.shopagent.response.ApiResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/api/chat")
    public ApiResponseDto<java.util.List<java.util.Map<String, Object>>> getChat(
            @RequestBody @Valid ChatRequestDTO dto
    ) {
        return chatService.getChat(dto);
    }

}
