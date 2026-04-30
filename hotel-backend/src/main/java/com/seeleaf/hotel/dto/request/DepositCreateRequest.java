package com.seeleaf.hotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositCreateRequest {

    @NotNull(message = "入住记录ID不能为空")
    private Long registrationId;

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private String slipNumber;

    private String transactionNo;

    private String authCode;
}
