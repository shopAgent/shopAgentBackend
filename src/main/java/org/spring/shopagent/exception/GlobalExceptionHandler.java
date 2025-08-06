package org.spring.shopagent.exception;


import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.response.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class )
    public ResponseEntity<ApiResponseDto<Void>> methodValidException(MethodArgumentNotValidException e) {
        ErrorResponse responseDto = ErrorResponse.of(e.getBindingResult());

        return ResponseEntity.badRequest().body(ResponseUtils.error(responseDto));
    }

    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ApiResponseDto<Void>> handleCustomException(CustomException e) {
        ErrorResponse responseDto;
        if (e.getCustomMessage() != null && !e.getCustomMessage().isEmpty())
            responseDto = ErrorResponse.of(e.getErrorType(), e.getCustomMessage());
        else
            responseDto = ErrorResponse.of(e.getErrorType());

        return ResponseEntity
                .status(e.getErrorType().getCode())
                .body(ResponseUtils.error(responseDto));
    }

}
