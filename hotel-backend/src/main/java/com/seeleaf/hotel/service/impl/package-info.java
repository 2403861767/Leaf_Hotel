/**
 * 业务逻辑层实现。
 * <p>
 * 每个 ServiceImpl 实现对应的接口，处理具体业务逻辑：
 * <ul>
 *   <li>分布式事务通过 {@code @Transactional} 保证原子性</li>
 *   <li>操作人信息从 SecurityContextHolder 获取</li>
 *   <li>账务操作同步写入 TransactionLog 流水</li>
 * </ul>
 */
package com.seeleaf.hotel.service.impl;
