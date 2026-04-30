package com.seeleaf.hotel.service.impl;

import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.dto.request.LoginRequest;
import com.seeleaf.hotel.dto.response.LoginResponse;
import com.seeleaf.hotel.dto.response.UserInfoResponse;
import com.seeleaf.hotel.entity.SysUser;
import com.seeleaf.hotel.mapper.SysUserMapper;
import com.seeleaf.hotel.security.JwtTokenProvider;
import com.seeleaf.hotel.security.model.LoginUser;
import com.seeleaf.hotel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现，处理用户登录认证、登出及当前登录用户信息查询。
 * <p>
 * 登录流程：AuthenticationManager 校验密码 → 签发 JWT Token → 返回用户信息。
 * 采用无状态设计，登出仅清除 SecurityContext（前端需配合移除 Token）。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            // 委托 AuthenticationManager 校验用户名密码（实际调用 CustomUserDetailsService）
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        if (!loginUser.isEnabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 认证通过后签发 JWT Token，有效期由 jwt.expiration 控制（默认 7200 秒）
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(loginUser.getUserId(), loginUser.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(7200);

        // 返回前端展示所需的用户基本信息（不含敏感字段）
        SysUser user = loginUser.getSysUser();
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRole(loginUser.getRoleKey());
        response.setUser(userInfo);

        return response;
    }

    @Override
    public void logout() {
        // 清除 SecurityContext；无状态架构下前端也需删除本地 Token
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserInfoResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getSysUser();

        // 返回当前用户完整信息（含权限列表），供前端做按钮级权限控制
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(loginUser.getRoleKey());
        response.setRoleName(user.getRoleName());
        response.setPermissions(loginUser.getPermissions());
        return response;
    }
}
