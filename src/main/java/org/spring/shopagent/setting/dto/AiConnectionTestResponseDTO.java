package org.spring.shopagent.setting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiConnectionTestResponseDTO {

    private boolean connection;

    private String provider;

    private String model;

}