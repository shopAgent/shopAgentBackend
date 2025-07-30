package org.spring.shopagent.setting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DbConnectionTestResponseDTO {

    private String databaseType;

    private String url;

    private String dbName;

    private String dbUserName;

    private String dbPassword;

    private Integer dbPort;

    private String version;

}