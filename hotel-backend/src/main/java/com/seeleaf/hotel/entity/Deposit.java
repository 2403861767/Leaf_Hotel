package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("deposit")
public class Deposit {

    @TableId
    private Long id;

    private Long registrationId;

    private String paymentMethod;

    private BigDecimal amount;

    private String slipNumber;

    private String transactionNo;

    private String authCode;

    /** active(有效) / refunded(已退还) */
    private String status;

    private Long operatorId;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
