package com.seeleaf.hotel.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CheckinRequest {

    private Long reservationId;

    @NotNull(message = "客人ID不能为空")
    private Long guestId;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    private Long roomTypeId;

    private LocalDate checkInDate;

    @NotNull(message = "预计离店日期不能为空")
    private LocalDate expectedCheckOutDate;

    @NotNull(message = "入住人数不能为空")
    private Integer guestCount;

    private BigDecimal depositAmount;

    @NotNull(message = "入住来源不能为空")
    private String source;

    private String bookingNumber;
}
