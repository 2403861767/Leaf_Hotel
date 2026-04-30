package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.dto.request.CheckoutRequest;
import com.seeleaf.hotel.dto.response.CheckoutResponse;
import com.seeleaf.hotel.entity.Registration;

public interface CheckoutService {

    CheckoutResponse createCheckout(CheckoutRequest request);

    IPage<Registration> queryCheckouts(int page, int pageSize, String keyword);

    CheckoutResponse calculateRefund(Long registrationId);
}
