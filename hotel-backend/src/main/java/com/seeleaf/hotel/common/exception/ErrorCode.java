package com.seeleaf.hotel.common.exception;

import lombok.Getter;

/**
 * 业务错误码枚举。
 * <p>
 * 每个错误对应唯一 code，前端可根据 code 做国际化或特定错误处理。
 * 分组规则：1xxx=系统/通用，2xxx=认证，3xxx=客房，4xxx=客人，5xxx=订单，6xxx=入住，7xxx=押金。
 */
@Getter
public enum ErrorCode {

    // ========== 通用 (1xxx) ==========
    /** 请求参数格式错误 */
    BAD_REQUEST(1001, "请求参数错误"),
    /** 资源不存在（兜底） */
    NOT_FOUND(1002, "资源不存在"),
    /** 服务器内部未预期异常 */
    INTERNAL_ERROR(1003, "服务器内部错误"),

    // ========== 认证/授权 (2xxx) ==========
    /** 未提供有效 Token */
    UNAUTHORIZED(2001, "未认证，请先登录"),
    /** Token 已过期 */
    TOKEN_EXPIRED(2002, "Token 已过期"),
    /** Token 无效或已被篡改 */
    TOKEN_INVALID(2003, "无效的 Token"),
    /** 已认证但无操作权限 */
    FORBIDDEN(2004, "无权限访问"),
    /** 用户名或密码错误 */
    USERNAME_OR_PASSWORD_ERROR(2005, "用户名或密码错误"),
    /** 账户已被管理员停用 */
    USER_DISABLED(2006, "账户已被停用"),

    // ========== 客房 (3xxx) ==========
    /** 客房不存在 */
    ROOM_NOT_FOUND(3001, "房间不存在"),
    /** 客房当前不可售（非 available 状态） */
    ROOM_NOT_AVAILABLE(3002, "房间不可售"),
    /** 并发操作导致房态冲突 */
    ROOM_STATUS_CONFLICT(3003, "房间状态冲突"),
    /** 非法的房态变更操作 */
    INVALID_ROOM_STATUS_TRANSITION(3004, "非法的房态变更"),

    // ========== 客人 (4xxx) ==========
    /** 客人档案不存在 */
    GUEST_NOT_FOUND(4001, "客人不存在"),

    // ========== 预订 (5xxx) ==========
    /** 预订订单不存在 */
    RESERVATION_NOT_FOUND(5001, "订单不存在"),

    // ========== 入住 (6xxx) ==========
    /** 入住登记记录不存在 */
    REGISTRATION_NOT_FOUND(6001, "入住记录不存在"),
    /** 该入住记录已完成退房，不可重复操作 */
    CHECKOUT_ALREADY_DONE(6002, "该入住记录已退房"),

    // ========== 押金 (7xxx) ==========
    /** 押金记录不存在 */
    DEPOSIT_NOT_FOUND(7001, "押金记录不存在"),
    /** 押金已退还，不可重复退款 */
    DEPOSIT_ALREADY_REFUNDED(7002, "押金已退还"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
