package org.spring.shopagent.response;

import lombok.Getter;

@Getter
public enum MsgType {

    DB_CONNECTION_SUCCESS("DB Connection Success"),
    DB_CONFIG_UPDATE_SUCCESS("DB Config Update Success"),
    ;

    private final String msg;

    MsgType(String msg) {
        this.msg = msg;
    }
}
