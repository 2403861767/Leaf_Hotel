package com.seeleaf.hotel.security;

import com.seeleaf.hotel.entity.SysRolePermission;
import com.seeleaf.hotel.entity.SysUser;
import com.seeleaf.hotel.mapper.SysRolePermissionMapper;
import com.seeleaf.hotel.mapper.SysUserMapper;
import com.seeleaf.hotel.security.model.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 用户加载服务，实现 {@link UserDetailsService}。
 * <p>
 * 认证流程：用户名 → 查询系统用户（左连角色表获取 roleKey/roleName）→ 加载角色权限 →
 * 组装为 {@link LoginUser}（包含用户信息 + 权限 code 列表）。
 * 权限 code 将映射为 GrantedAuthority，供 {@code @PreAuthorize("hasAuthority('xxx:yyy')")} 判断。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<SysRolePermission> permissions = sysRolePermissionMapper.selectByRoleId(user.getRoleId());
        List<String> permissionCodes = permissions.stream()
                .map(SysRolePermission::getPermissionCode)
                .collect(Collectors.toList());

        return new LoginUser(user, permissionCodes);
    }
}
