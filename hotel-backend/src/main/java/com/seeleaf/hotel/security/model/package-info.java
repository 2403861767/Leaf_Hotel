/**
 * 安全模型，主要包含 {@code LoginUser}。
 * <p>
 * LoginUser 桥接系统用户实体（SysUser）与 Spring Security 框架（UserDetails），
 * 将角色权限 code 列表转为 GrantedAuthority，支持 {@code @PreAuthorize} 注解判断。
 */
package com.seeleaf.hotel.security.model;
