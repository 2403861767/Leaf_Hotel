/**
 * 数据传输对象，分层 API 请求与响应。
 * <ul>
 *   <li>request — 请求参数封装（含 javax.validation 校验注解）</li>
 *   <li>response — 响应数据封装（Service 层返回给 Controller 的特定结构）</li>
 * </ul>
 * 与 Entity 分离，避免内部数据模型暴露给前端。
 */
package com.seeleaf.hotel.dto;
