package com.seeleaf.hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutRequest {

    @NotNull(message = "入住记录ID不能为空")
    private Long registrationId;

    private List<AdditionalCharge> additionalCharges;

    @Data
    public static class AdditionalCharge {
        private String item;
        private BigDecimal amount;
    }
}
