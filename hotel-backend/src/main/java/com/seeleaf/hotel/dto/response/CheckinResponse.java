package com.seeleaf.hotel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckinResponse {

    private Long registrationId;
    private String roomNumber;
    private String status;
    private LocalDateTime createdAt;
}
