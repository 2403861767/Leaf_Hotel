package com.seeleaf.hotel.common.constant;

public interface Constants {

    String TOKEN_PREFIX = "Bearer ";
    String TOKEN_HEADER = "Authorization";
    String LOGIN_USER_KEY = "login_user";

    String ROOM_STATUS_AVAILABLE = "available";
    String ROOM_STATUS_OCCUPIED = "occupied";
    String ROOM_STATUS_DIRTY = "dirty";
    String ROOM_STATUS_MAINTENANCE = "maintenance";

    String REG_STATUS_IN_HOUSE = "in_house";
    String REG_STATUS_CHECKED_OUT = "checked_out";

    String DEPOSIT_STATUS_ACTIVE = "active";
    String DEPOSIT_STATUS_REFUNDED = "refunded";
}
