package com.seeleaf.hotel.common.response;

import lombok.Data;

/**
 * 统一 API 响应格式。
 * <p>
 * 所有接口均返回此结构：{@code {code, message, data}}。
 * <ul>
 *   <li>成功：code=200，message="success"，data 为业务数据</li>
 *   <li>失败：code 为业务错误码，message 为错误描述，data=null</li>
 * </ul>
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result() {}

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
