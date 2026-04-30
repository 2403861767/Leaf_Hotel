package com.seeleaf.hotel.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CheckoutResponse {

    private Long registrationId;
    private String roomNumber;
    private BigDecimal roomCharge;
    private BigDecimal additionalCharges;
    private BigDecimal totalCharge;
    private BigDecimal depositAmount;
    private BigDecimal refundAmount;
    private LocalDateTime checkoutTime;
}
