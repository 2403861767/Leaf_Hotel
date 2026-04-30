/**
 * 数据库实体映射。
 * <p>
 * 每个实体对应一张数据库表，使用 MyBatis-Plus 注解：
 * <ul>
 *   <li>{@code @TableName} — 指定表名</li>
 *   <li>{@code @TableId} — 主键</li>
 *   <li>{@code @TableLogic} — 逻辑删除字段</li>
 *   <li>{@code @Version} — 乐观锁版本号</li>
 *   <li>{@code @TableField(exist = false)} — 非持久化字段（联表查询注入）</li>
 * </ul>
 * 字段映射规则：数据库下划线命名 → Java 驼峰命名。
 */
package com.seeleaf.hotel.entity;
