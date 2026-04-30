package com.seeleaf.hotel.security.model;

import com.seeleaf.hotel.entity.SysUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security {@link UserDetails} 实现，桥接系统用户实体与安全框架。
 * <p>
 * 主要职责：
 * <ul>
 *   <li>将 SysUser 包装为 UserDetails，供 AuthenticationManager 使用</li>
 *   <li>将权限 code 列表转为 GrantedAuthority，支持 {@code @PreAuthorize} 注解</li>
 *   <li>提供 getUserId() / getRoleKey() 等便利方法供业务层使用</li>
 * </ul>
 *
 * @see CustomUserDetailsService 组装此对象
 * @see JwtTokenProvider 签发 Token 后从中提取用户信息
 */
@Getter
public class LoginUser implements UserDetails {

    private final SysUser sysUser;
    private final List<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUser(SysUser sysUser, List<String> permissions) {
        this.sysUser = sysUser;
        this.permissions = permissions;
        this.authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Long getUserId() {
        return sysUser.getId();
    }

    public String getRoleKey() {
        return sysUser.getRoleKey();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return sysUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // sys_user.status 字段：1=启用，0=停用
        return sysUser.getStatus() == 1;
    }
}
