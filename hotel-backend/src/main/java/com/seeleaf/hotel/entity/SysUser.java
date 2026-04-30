package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId
    private Long id;

    private String username;

    private String passwordHash;

    private String realName;

    private Integer roleId;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 非数据库字段：由 SysUserMapper.selectByUsername 的 LEFT JOIN 注入 */
    @TableField(exist = false)
    private String roleKey;

    /** 非数据库字段：由 SysUserMapper.selectByUsername 的 LEFT JOIN 注入 */
    @TableField(exist = false)
    private String roleName;
}
