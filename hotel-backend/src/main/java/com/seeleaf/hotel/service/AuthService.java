package com.seeleaf.hotel.service;

import com.seeleaf.hotel.dto.request.LoginRequest;
import com.seeleaf.hotel.dto.response.LoginResponse;
import com.seeleaf.hotel.dto.response.UserInfoResponse;

/**
 * 认证服务接口。
 * <p>负责用户登录认证、登出及当前用户信息查询。采用 JWT 无状态方案，不维护服务端会话。</p>
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout();

    UserInfoResponse getCurrentUser();
}
