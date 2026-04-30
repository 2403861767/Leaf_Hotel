package com.seeleaf.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeleaf.hotel.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户，同时左连角色表加载 roleKey/roleName。
     * <p>
     * roleKey/roleName 不在 sys_user 表中，通过 SQL 联表查询注入到
     * SysUser 的 {@code @TableField(exist = false)} 字段中，
     * 供 {@link com.seeleaf.hotel.security.CustomUserDetailsService} 构建 LoginUser。
     */
    @Select("SELECT u.*, r.role_key, r.role_name FROM sys_user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.role_id " +
            "WHERE u.username = #{username} AND u.is_deleted = 0")
    SysUser selectByUsername(@Param("username") String username);
}
