package org.spring.shopagent.setting.dto;

import lombok.Data;

@Data
public class TableColumnResponseDTO {

    private String columnName;
    private String dataType;
    private String isNullable;
    private String columnDefault;
    private String columnComment;
}