package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("guest")
public class Guest {

    @TableId
    private Long id;

    private String name;

    private String idType;

    private String idNumber;

    private String phone;

    private String address;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
