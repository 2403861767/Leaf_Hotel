package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {

    @TableId(value = "role_id")
    private Integer roleId;

    private String roleName;

    private String roleKey;

    private String description;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;
}
