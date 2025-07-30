package org.spring.shopagent.response;

import org.spring.shopagent.exception.ErrorResponse;

public class ResponseUtils {

    public static <T> ApiResponseDto<T> ok(MsgType msg, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .data(data)
                .msg(msg.getMsg())
                .build();
    }

    public static ApiResponseDto<Void> ok(MsgType msg) {
        return ApiResponseDto.<Void>builder()
                .success(true)
                .msg(msg.getMsg())
                .build();
    }

    public static ApiResponseDto<Void> error(ErrorResponse error) {
        return ApiResponseDto.<Void>builder()
                .success(false)
                .error(error)
                .build();
    }
}
