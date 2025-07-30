package org.spring.shopagent.info.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DatabaseInfoDTO {

    private String aiProvider;

    private String token;

    private String databaseType;

    private String url;

    private String dbName;

    private String dbUserName;

    private String dbPassword;

    private Integer dbPort;

    private String userName;

    private String userEmail;

}
