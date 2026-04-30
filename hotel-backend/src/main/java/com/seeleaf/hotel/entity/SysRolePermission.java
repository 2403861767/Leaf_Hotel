package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class SysRolePermission {

    @TableId
    private Long id;

    private Integer roleId;

    private String permissionCode;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;
}
