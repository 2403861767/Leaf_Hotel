package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room_type")
public class RoomType {

    @TableId
    private Long id;

    private String typeName;

    private BigDecimal basePrice;

    private Integer maxGuests;

    private String bedType;

    private BigDecimal area;

    private String description;

    private Integer status;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;
}
