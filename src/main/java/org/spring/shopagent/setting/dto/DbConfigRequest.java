package org.spring.shopagent.setting.dto;

import lombok.Data;

@Data
public class DbConfigRequest {
    private String url;
    private String username;
    private String password;
}