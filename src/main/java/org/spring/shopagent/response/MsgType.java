package org.spring.shopagent.response;

import lombok.Getter;

@Getter
public enum MsgType {

    DATA_SELECT_SUCCESS("Data Select Success"),
    DB_CONNECTION_SUCCESS("DB Connection Success"),
    DB_CONFIG_UPDATE_SUCCESS("DB Config Update Success"),
    DB_CONNECTION_DISCONNECTED("DB Connection Disconnected"),
    SEARCH_CONFIG_INSERT_SUCCESS("Search Config Insert Success"),
    SEARCH_CONFIG_UPDATE_SUCCESS("Search Config Update Success"),
    AI_CONNECTION_OK("AI Connection OK"),;

    private final String msg;

    MsgType(String msg) {
        this.msg = msg;
    }
}
