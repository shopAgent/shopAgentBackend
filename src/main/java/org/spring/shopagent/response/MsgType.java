package org.spring.shopagent.response;

import lombok.Getter;

@Getter
public enum MsgType {

    DB_CONNECTION_SUCCESS("DB 연결 성공"),
    DB_CONFIG_UPDATE_SUCCESS("DB 설정 업데이트 성공"),
    ;

    private final String msg;

    MsgType(String msg) {
        this.msg = msg;
    }
}
