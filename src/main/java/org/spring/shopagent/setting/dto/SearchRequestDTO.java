package org.spring.shopagent.setting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;

@Data
public class SearchRequestDTO {

    @NotEmpty(message = "Search query cannot be empty")
    @Size(max = 100, message = "Search query must be less than 100 characters")
    private String toolName;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotEmpty(message = "table name cannot be empty")
    @Size(max = 100, message = "Table name must be less than 100 characters")
    private String tableName;

    @NotEmpty(message = "Product name cannot be empty")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String productName;

    @NotEmpty(message = "Product image cannot be empty")
    @Size(max = 500, message = "Product image URL must be less than 500 characters")
    private String productImage;

    private Long idxSearchConfig;

}