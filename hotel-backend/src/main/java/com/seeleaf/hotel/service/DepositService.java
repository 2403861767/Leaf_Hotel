package com.seeleaf.hotel.service;

import com.seeleaf.hotel.dto.request.DepositCreateRequest;
import com.seeleaf.hotel.entity.Deposit;

import java.util.List;

/**
 * 押金管理服务接口。
 * <p>押金与入住登记关联。同一登记可多次缴纳押金，余额累加。退房时从余额中扣除房费与杂费后可用部分原路退还。
 * 每笔押金操作会写入交易流水日志，确保账务可追溯。</p>
 */
public interface DepositService {

    Deposit createDeposit(DepositCreateRequest request);

    List<Deposit> queryDeposits(Long registrationId);

    Deposit refundDeposit(Long id);
}
