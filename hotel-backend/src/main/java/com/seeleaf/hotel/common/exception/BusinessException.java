package com.seeleaf.hotel.common.exception;

import lombok.Getter;

/**
 * 业务异常，触发时由 {@link GlobalExceptionHandler} 捕获并返回统一错误响应。
 * <p>
 * 优先使用 {@link #BusinessException(ErrorCode)} 构造，确保错误码与 message 一致。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
}
