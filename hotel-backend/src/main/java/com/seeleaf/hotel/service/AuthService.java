package com.seeleaf.hotel.service;

import com.seeleaf.hotel.dto.request.LoginRequest;
import com.seeleaf.hotel.dto.response.LoginResponse;
import com.seeleaf.hotel.dto.response.UserInfoResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout();

    UserInfoResponse getCurrentUser();
}
