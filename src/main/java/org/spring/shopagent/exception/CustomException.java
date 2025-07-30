package org.spring.shopagent.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorType errorType;
    private final String customMessage;

    public CustomException(ErrorType errorType) {
        super(errorType.getMsg());
        this.errorType = errorType;
        this.customMessage = null;
    }

    public CustomException(ErrorType errorType, String customMessage) {
        super(errorType.getMsg() + " - " + customMessage);
        this.errorType = errorType;
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage == null ? errorType.getMsg() : errorType.getMsg() + " - " + customMessage;
    }
}