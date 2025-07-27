package org.spring.shopagent.exception;

import lombok.Getter;

@Getter
public enum ErrorType {

    DB_CONNECTION_ERROR(501, "DB Connection Failed"),
    DB_CONNECTION_FILE_SAVE_ERROR(502, "DB Connection File Save Failed"),
    DB_CONFIG_NOT_FOUND(503, "DB Config Not Found"),
    DB_DATABASE_NOT_FOUND(504, "Database Not Found"),
    ;

    private int code;
    private String msg;

    ErrorType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
