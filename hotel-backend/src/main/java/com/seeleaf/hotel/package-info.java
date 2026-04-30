/**
 * Leaf-Hotel 酒店前台管理系统 — 后端根包。
 * <p>
 * 基于 Spring Boot 3.5 + MyBatis-Plus + JWT 的前后端分离系统，
 * 覆盖客房预订、入住登记、房态管理、账务处理等前台核心业务流程。
 * <p>
 * 分层说明：
 * <ul>
 *   <li>controller — RESTful API 入口，通过 @PreAuthorize 控制权限</li>
 *   <li>service — 业务逻辑层，接口+实现分离</li>
 *   <li>mapper — MyBatis-Plus 数据访问层</li>
 *   <li>entity — 数据库实体映射</li>
 *   <li>dto — 请求/响应数据传输对象</li>
 *   <li>security — JWT 无状态认证 + Spring Security RBAC</li>
 *   <li>config — 框架集成配置</li>
 *   <li>common — 公共基础设施（统一响应、异常处理、常量）</li>
 * </ul>
 */
package com.seeleaf.hotel;
