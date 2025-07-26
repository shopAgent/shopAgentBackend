package org.spring.shopagent.exception;

import lombok.Getter;

@Getter
public enum ErrorType {

    DB_CONNECTION_ERROR(501, "DB 연결에 실패했습니다."),
    DB_CONNECTION_FILE_SAVE_ERROR(502, "DB 연결 정보 저장에 실패했습니다."),
    DB_CONFIG_NOT_FOUND(503, "DB 연결 정보가 존재하지 않습니다."),
    ;

    private int code;
    private String msg;

    ErrorType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
