/**
 * RESTful API 控制器层。
 * <p>
 * 接收前端请求，调用 Service 层处理业务，返回统一 {@code Result} / {@code PageResult} 响应。
 * 权限通过 {@code @PreAuthorize} 注解控制，权限 code 格式为 {@code 模块:操作}。
 * <p>
 * 接口前缀统一为 {@code /api/v1}，包括：
 * <ul>
 *   <li>/auth — 认证（登录/登出/当前用户）</li>
 *   <li>/checkins — 入住登记</li>
 *   <li>/checkouts — 退房结账</li>
 *   <li>/deposits — 押金管理</li>
 *   <li>/guests — 客人管理</li>
 *   <li>/rooms — 客房管理（房态图）</li>
 * </ul>
 */
package com.seeleaf.hotel.controller;
