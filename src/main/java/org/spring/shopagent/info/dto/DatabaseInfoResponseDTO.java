package org.spring.shopagent.info.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DatabaseInfoResponseDTO {

    private String databaseName;

    private String tableName;

    private String columnName;

}
