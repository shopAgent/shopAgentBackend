package org.spring.shopagent.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {

    @NotEmpty(message = "Message cannot be empty")
    private String message;
}
