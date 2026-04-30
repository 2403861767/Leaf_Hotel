package com.seeleaf.hotel.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {

    private Long id;
    private String username;
    private String realName;
    private String role;
    private String roleName;
    private List<String> permissions;
}
