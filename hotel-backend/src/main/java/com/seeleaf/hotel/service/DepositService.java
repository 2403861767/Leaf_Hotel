package com.seeleaf.hotel.service;

import com.seeleaf.hotel.dto.request.DepositCreateRequest;
import com.seeleaf.hotel.entity.Deposit;

import java.util.List;

public interface DepositService {

    Deposit createDeposit(DepositCreateRequest request);

    List<Deposit> queryDeposits(Long registrationId);

    Deposit refundDeposit(Long id);
}
