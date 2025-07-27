package org.spring.shopagent.info.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseNameRequestDTO {

    @NotEmpty(message = "Database name cannot be empty")
    private String databaseName;

}
