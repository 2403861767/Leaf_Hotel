/**
 * 业务逻辑层接口定义。
 * <p>
 * 按业务模块拆分，每个 service 定义对应模块的完整业务操作契约：
 * <ul>
 *   <li>AuthService — 登录/登出/当前用户</li>
 *   <li>CheckinService — 入住登记/查询/修改</li>
 *   <li>CheckoutService — 退房结账/费用计算</li>
 *   <li>DepositService — 押金收取/退还</li>
 *   <li>GuestService — 客人 CRUD</li>
 *   <li>RoomService — 房态图/客房查询</li>
 * </ul>
 */
package com.seeleaf.hotel.service;
