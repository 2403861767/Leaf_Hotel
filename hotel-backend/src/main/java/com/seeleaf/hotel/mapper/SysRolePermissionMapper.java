package com.seeleaf.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seeleaf.hotel.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 查询指定角色的所有权限 code。
     * <p>
     * 返回的 permissionCode 列表最终映射为 GrantedAuthority，
     * 供 {@code @PreAuthorize("hasAuthority('xxx:yyy')")} 判断权限。
     */
    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId} AND is_deleted = 0")
    List<SysRolePermission> selectByRoleId(@Param("roleId") Integer roleId);
}
