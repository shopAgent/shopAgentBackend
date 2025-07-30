package org.spring.shopagent.setting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DbConnectionTestRequestDTO {

    @NotEmpty(message = "Database type cannot be empty")
    @Pattern(regexp = "MySQL|MSSQL|PostgreSQL|SQLite", message = "Database type must be one of: MySQL, MsSQL, PostgreSQL, SQLite")
    private String databaseType;

    @NotEmpty(message = "URL cannot be empty")
    private String url;

    @NotEmpty(message = "Database name cannot be empty")
    private String dbName;

    @NotEmpty(message = "Database name cannot be empty")
    private String dbUserName;

    @NotEmpty(message = "Password cannot be empty")
    private String dbPassword;

    @NotNull(message = "Database port cannot be null")
    @Min(value = 1, message = "Database port must be greater than 0")
    private Integer dbPort;

}