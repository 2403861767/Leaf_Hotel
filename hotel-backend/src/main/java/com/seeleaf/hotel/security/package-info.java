/**
 * 安全认证层 — JWT 无状态认证 + Spring Security RBAC。
 * <p>
 * 认证流程：
 * <ol>
 *   <li>JwtAuthenticationFilter 从请求头提取 Bearer Token</li>
 *   <li>JwtTokenProvider 解析并验证 Token 签名</li>
 *   <li>CustomUserDetailsService 加载用户信息与权限列表</li>
 *   <li>LoginUser 作为 UserDetails 实现桥接系统用户与 Spring Security</li>
 *   <li>SecurityConfig 配置过滤链（无状态 Session、接口放行规则）</li>
 * </ol>
 */
package com.seeleaf.hotel.security;
