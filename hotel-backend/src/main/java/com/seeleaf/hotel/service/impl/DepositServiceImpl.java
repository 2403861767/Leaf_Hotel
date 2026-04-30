package com.seeleaf.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seeleaf.hotel.common.constant.Constants;
import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.dto.request.DepositCreateRequest;
import com.seeleaf.hotel.entity.Deposit;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.entity.TransactionLog;
import com.seeleaf.hotel.mapper.DepositMapper;
import com.seeleaf.hotel.mapper.RegistrationMapper;
import com.seeleaf.hotel.mapper.TransactionLogMapper;
import com.seeleaf.hotel.security.model.LoginUser;
import com.seeleaf.hotel.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 押金服务实现，处理押金收取、查询与退还操作。
 * <p>
 * 每次押金变动（收/退）均同步更新对应入住记录的 depositAmount 余额，
 * 并写入账务流水（transaction_log）供对账使用。
 */
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositMapper depositMapper;
    private final RegistrationMapper registrationMapper;
    private final TransactionLogMapper transactionLogMapper;

    @Override
    @Transactional
    public Deposit createDeposit(DepositCreateRequest request) {
        Registration registration = registrationMapper.selectById(request.getRegistrationId());
        if (registration == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 在修改前记录旧余额，用于流水对账
        BigDecimal balanceBefore = registration.getDepositAmount();

        // 1. 创建押金记录
        Deposit deposit = new Deposit();
        deposit.setRegistrationId(request.getRegistrationId());
        deposit.setPaymentMethod(request.getPaymentMethod());
        deposit.setAmount(request.getAmount());
        deposit.setSlipNumber(request.getSlipNumber());
        deposit.setTransactionNo(request.getTransactionNo());
        deposit.setAuthCode(request.getAuthCode());
        deposit.setStatus(Constants.DEPOSIT_STATUS_ACTIVE);
        deposit.setOperatorId(loginUser.getUserId());
        depositMapper.insert(deposit);

        // 2. 累加押金到入住记录的 depositAmount 字段
        BigDecimal newDepositTotal = balanceBefore.add(request.getAmount());
        registration.setDepositAmount(newDepositTotal);
        registrationMapper.updateById(registration);

        // 3. 记录账务流水（押金入账）
        TransactionLog log = new TransactionLog();
        log.setRegistrationId(registration.getId());
        log.setTransactionType("deposit");
        log.setAmount(request.getAmount());
        log.setBalanceBefore(balanceBefore);
        log.setBalanceAfter(newDepositTotal);
        log.setDescription("押金 - " + request.getPaymentMethod());
        log.setOperatorId(loginUser.getUserId());
        transactionLogMapper.insert(log);

        return deposit;
    }

    @Override
    public List<Deposit> queryDeposits(Long registrationId) {
        LambdaQueryWrapper<Deposit> wrapper = new LambdaQueryWrapper<>();
        if (registrationId != null) {
            wrapper.eq(Deposit::getRegistrationId, registrationId);
        }
        wrapper.orderByDesc(Deposit::getCreatedAt);
        return depositMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Deposit refundDeposit(Long id) {
        Deposit deposit = depositMapper.selectById(id);
        if (deposit == null) {
            throw new BusinessException(ErrorCode.DEPOSIT_NOT_FOUND);
        }
        if (Constants.DEPOSIT_STATUS_REFUNDED.equals(deposit.getStatus())) {
            throw new BusinessException(ErrorCode.DEPOSIT_ALREADY_REFUNDED);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 1. 标记押金记录为已退还
        deposit.setStatus(Constants.DEPOSIT_STATUS_REFUNDED);
        depositMapper.updateById(deposit);

        // 2. 从入住记录的 depositAmount 中扣除该笔押金
        Registration registration = registrationMapper.selectById(deposit.getRegistrationId());
        if (registration != null) {
            BigDecimal newDepositTotal = registration.getDepositAmount().subtract(deposit.getAmount());
            registration.setDepositAmount(newDepositTotal);
            registrationMapper.updateById(registration);
        }

        // 3. 记录退款流水
        TransactionLog log = new TransactionLog();
        log.setRegistrationId(deposit.getRegistrationId());
        log.setTransactionType("refund");
        log.setAmount(deposit.getAmount().negate());
        log.setBalanceBefore(registration != null ? registration.getDepositAmount().add(deposit.getAmount()) : BigDecimal.ZERO);
        log.setBalanceAfter(registration != null ? registration.getDepositAmount() : BigDecimal.ZERO);
        log.setDescription("退押金 - " + deposit.getPaymentMethod());
        log.setOperatorId(loginUser.getUserId());
        transactionLogMapper.insert(log);

        return deposit;
    }
}
