package com.seeleaf.hotel.controller;

import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.dto.request.LoginRequest;
import com.seeleaf.hotel.dto.response.LoginResponse;
import com.seeleaf.hotel.dto.response.UserInfoResponse;
import com.seeleaf.hotel.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证接口。
 * <p>
 * POST /login — 登录获取 JWT Token<br>
 * POST /logout — 登出（清除 SecurityContext）<br>
 * GET /me — 获取当前登录用户信息
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录。
     * <p>
     * 校验用户名密码，认证通过后签发 JWT Token。前端需保存 Token，后续请求在
     * Authorization 头中携带 {@code Bearer <token>} 访问需认证的接口。
     *
     * @param request 登录请求（username + password）
     * @return token + 用户基本信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    /**
     * 用户登出。
     * <p>
     * 清除服务器端 SecurityContext。由于采用无状态 JWT，前端也需同步删除本地保存的 Token。
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 获取当前登录用户信息。
     * <p>
     * 根据请求头中的 Bearer Token 解析用户身份，返回完整信息（含角色名与权限 code 列表），
     * 供前端做按钮级权限控制。
     */
    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }
}
