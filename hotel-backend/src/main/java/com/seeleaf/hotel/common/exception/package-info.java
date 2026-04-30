/**
 * 异常处理体系。
 * <ul>
 *   <li>ErrorCode — 唯一业务错误码枚举，前端据此做国际化或特定错误处理</li>
 *   <li>BusinessException — 业务异常，触发后由 GlobalExceptionHandler 捕获</li>
 *   <li>GlobalExceptionHandler — 全局异常处理器，统一返回 {@code Result} 格式</li>
 * </ul>
 */
package com.seeleaf.hotel.common.exception;
