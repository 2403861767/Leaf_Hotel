package com.seeleaf.hotel.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String role;
    }
}
