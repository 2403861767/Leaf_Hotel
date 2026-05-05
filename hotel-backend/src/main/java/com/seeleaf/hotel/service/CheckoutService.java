package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.dto.request.CheckoutRequest;
import com.seeleaf.hotel.dto.response.CheckoutResponse;
import com.seeleaf.hotel.entity.Registration;

/**
 * 退房结算服务接口。
 * <p>核心结算链路：计算房费（按过夜天数）→ 扣减押金余额 → 退还剩余押金 → 更新房态为脏房。
 * 同时提供退房记录查询和预计算退款金额功能（供预览确认）。</p>
 */
public interface CheckoutService {

    CheckoutResponse createCheckout(CheckoutRequest request);

    IPage<Registration> queryCheckouts(int page, int pageSize, String keyword);

    CheckoutResponse calculateRefund(Long registrationId);
}
