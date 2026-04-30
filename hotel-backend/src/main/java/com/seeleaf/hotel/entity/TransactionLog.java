package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("transaction_log")
public class TransactionLog {

    @TableId
    private Long id;

    private Long registrationId;

    /**
     * 交易类型：deposit(存入) / refund(退款) / room_charge(房费扣减) / reversal(冲正)
     */
    private String transactionType;

    /** 金额（正数=收入，负数=支出） */
    private BigDecimal amount;

    /** 变动前押金余额（用于对账） */
    private BigDecimal balanceBefore;

    /** 变动后押金余额 */
    private BigDecimal balanceAfter;

    private String description;

    private Long operatorId;

    private Long reversalOf;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;
}
