package org.spring.shopagent.setting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoResponseDTO {
    
    private String tableName;
    private Long rowCount;
    private Integer columnCount;
    private String tableComment;
    
}