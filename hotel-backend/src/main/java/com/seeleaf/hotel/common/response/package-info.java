/**
 * 统一 API 响应模型。
 * <ul>
 *   <li>Result — 标准响应 {@code {code, message, data}}，适用于非分页接口</li>
 *   <li>PageResult — 分页响应，data 包含 list/total/page/pageSize</li>
 * </ul>
 * 所有 Controller 方法应返回 Result 或 PageResult，保持接口契约一致。
 */
package com.seeleaf.hotel.common.response;
