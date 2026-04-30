/**
 * API 响应数据 DTO。
 * <p>
 * Service 层返回给 Controller 的特定结构，Controller 再包装为 {@code Result<T>} 返回给前端。
 * 每个 Response DTO 只包含前端需要的字段，避免泄漏内部数据。
 */
package com.seeleaf.hotel.dto.response;
