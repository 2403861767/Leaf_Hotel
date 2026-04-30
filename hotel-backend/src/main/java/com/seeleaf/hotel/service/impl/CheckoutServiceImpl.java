package com.seeleaf.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeleaf.hotel.common.constant.Constants;
import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.dto.request.CheckoutRequest;
import com.seeleaf.hotel.dto.response.CheckoutResponse;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.entity.RoomType;
import com.seeleaf.hotel.entity.TransactionLog;
import com.seeleaf.hotel.mapper.RegistrationMapper;
import com.seeleaf.hotel.mapper.RoomMapper;
import com.seeleaf.hotel.mapper.RoomTypeMapper;
import com.seeleaf.hotel.mapper.TransactionLogMapper;
import com.seeleaf.hotel.security.model.LoginUser;
import com.seeleaf.hotel.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 退房结账服务实现，处理客人退房时的费用结算、押金抵扣与退款。
 * <p>
 * 退房流程：计算住宿天数 → 按房型房价计算房费 → 叠加杂费 → 押金抵扣 → 退差额。
 * 退房后客房状态变为 dirty（待清洁），入住记录状态变为 checked_out（已退房）。
 */
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final RegistrationMapper registrationMapper;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final TransactionLogMapper transactionLogMapper;

    @Override
    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest request) {
        // 1. 校验入住记录是否存在且未退房
        Registration registration = registrationMapper.selectById(request.getRegistrationId());
        if (registration == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
        }
        if (Constants.REG_STATUS_CHECKED_OUT.equals(registration.getStatus())) {
            throw new BusinessException(ErrorCode.CHECKOUT_ALREADY_DONE);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 2. 计算实际住宿天数（最少按 1 天计）
        long nights = Math.max(1, ChronoUnit.DAYS.between(
                registration.getCheckInTime().toLocalDate(),
                LocalDateTime.now().toLocalDate()));

        // 3. 根据客房关联的房型取 basePrice 计算房费
        Room room = roomMapper.selectById(registration.getRoomId());
        BigDecimal roomPrice = BigDecimal.ZERO;
        if (room != null) {
            RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
            if (roomType != null) {
                roomPrice = roomType.getBasePrice();
            }
        }

        BigDecimal roomCharge = roomPrice.multiply(BigDecimal.valueOf(nights));

        // 4. 统计退房时产生的额外杂费（迷你吧、赔偿等）
        BigDecimal additionalTotal = request.getAdditionalCharges() != null
                ? request.getAdditionalCharges().stream()
                .map(CheckoutRequest.AdditionalCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        // 5. 总费用 = 房费 + 杂费，押金余额 = 押金 - 总费用（负数表示需补交）
        BigDecimal totalCharge = roomCharge.add(additionalTotal);
        BigDecimal refundAmount = registration.getDepositAmount().subtract(totalCharge);

        // 6. 更新入住记录状态为 checked_out，记录实际退房时间
        registration.setActualCheckOutTime(LocalDateTime.now());
        registration.setStatus(Constants.REG_STATUS_CHECKED_OUT);
        registrationMapper.updateById(registration);

        // 7. 客房状态变为 dirty（待清洁），通知客房部打扫
        room.setStatus(Constants.ROOM_STATUS_DIRTY);
        roomMapper.updateById(room);

        // 8. 记录房费扣减流水
        TransactionLog chargeLog = new TransactionLog();
        chargeLog.setRegistrationId(registration.getId());
        chargeLog.setTransactionType("room_charge");
        chargeLog.setAmount(roomCharge.negate());
        chargeLog.setBalanceBefore(registration.getDepositAmount());
        chargeLog.setBalanceAfter(registration.getDepositAmount().subtract(totalCharge));
        chargeLog.setDescription("房费扣减（" + nights + "晚 x " + roomPrice + "）");
        chargeLog.setOperatorId(loginUser.getUserId());
        transactionLogMapper.insert(chargeLog);

        // 9. 押金有结余时自动退款并记录退款流水
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            TransactionLog refundLog = new TransactionLog();
            refundLog.setRegistrationId(registration.getId());
            refundLog.setTransactionType("refund");
            refundLog.setAmount(refundAmount.negate());
            refundLog.setBalanceBefore(registration.getDepositAmount().subtract(totalCharge));
            refundLog.setBalanceAfter(BigDecimal.ZERO);
            refundLog.setDescription("退押金余额");
            refundLog.setOperatorId(loginUser.getUserId());
            transactionLogMapper.insert(refundLog);
        }

        CheckoutResponse response = new CheckoutResponse();
        response.setRegistrationId(registration.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomCharge(roomCharge);
        response.setAdditionalCharges(additionalTotal);
        response.setTotalCharge(totalCharge);
        response.setDepositAmount(registration.getDepositAmount());
        response.setRefundAmount(refundAmount);
        response.setCheckoutTime(LocalDateTime.now());
        return response;
    }

    @Override
    public IPage<Registration> queryCheckouts(int page, int pageSize, String keyword) {
        Page<Registration> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Registration::getStatus, Constants.REG_STATUS_CHECKED_OUT); // 仅查询已退房记录
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Registration::getId, keyword);
        }
        wrapper.orderByDesc(Registration::getActualCheckOutTime);
        return registrationMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public CheckoutResponse calculateRefund(Long registrationId) {
        // 预计算退款金额（退房前预览），逻辑同 createCheckout 但不实际写入数据库
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
        }

        long nights = Math.max(1, ChronoUnit.DAYS.between(
                registration.getCheckInTime().toLocalDate(),
                LocalDateTime.now().toLocalDate()));

        Room room = roomMapper.selectById(registration.getRoomId());
        BigDecimal roomPrice = BigDecimal.ZERO;
        if (room != null) {
            RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
            if (roomType != null) {
                roomPrice = roomType.getBasePrice();
            }
        }

        BigDecimal roomCharge = roomPrice.multiply(BigDecimal.valueOf(nights));
        BigDecimal refundAmount = registration.getDepositAmount().subtract(roomCharge);

        CheckoutResponse response = new CheckoutResponse();
        response.setRegistrationId(registrationId);
        response.setDepositAmount(registration.getDepositAmount());
        response.setRoomCharge(roomCharge);
        response.setTotalCharge(roomCharge);
        response.setRefundAmount(refundAmount);
        return response;
    }
}
