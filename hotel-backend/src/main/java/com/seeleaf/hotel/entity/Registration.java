package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {

    @TableId
    private Long id;

    private Long reservationId;

    private Long guestId;

    private Long roomId;

    private LocalDateTime checkInTime;

    private LocalDateTime expectedCheckOutTime;

    private LocalDateTime actualCheckOutTime;

    private Integer guestCount;

    private BigDecimal depositAmount;

    private Long operatorId;

    /** 入住来源：walk_in(散客) / reservation(预订) / online(线上) */
    private String source;

    /** in_house(在住) / checked_out(已退房) */
    private String status;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;
}
