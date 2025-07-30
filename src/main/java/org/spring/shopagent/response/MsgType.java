package org.spring.shopagent.response;

import lombok.Getter;

@Getter
public enum MsgType {

    DATA_SELECT_SUCCESS("Data Select Success"),
    DB_CONNECTION_SUCCESS("DB Connection Success"),
    DB_CONFIG_UPDATE_SUCCESS("DB Config Update Success"),
    DB_CONNECTION_DISCONNECTED("DB Connection Disconnected"),
    ;

    private final String msg;

    MsgType(String msg) {
        this.msg = msg;
    }
}
