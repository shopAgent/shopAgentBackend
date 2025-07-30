package org.spring.shopagent.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.spring.shopagent.exception.ErrorResponse;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {

    private boolean success;
    private T data;
    private String msg;
    private ErrorResponse error;

    @Builder
    public ApiResponseDto(boolean success, T data, String msg, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.msg = msg;
        this.error = error;
    }
}
