package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {

    @TableId
    private Long id;

    private String bookingNumber;

    private Long guestId;

    private Long roomTypeId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    /** 预订状态：pending(待确认) / confirmed(已确认) / checked_in(已入住) / canceled(已取消) */
    private String status;

    /** 预订来源：同入住来源枚举 walk_in / phone / front_desk / manager / online_direct / online_ota / contract */
    private String source;

    private BigDecimal depositAmount;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
