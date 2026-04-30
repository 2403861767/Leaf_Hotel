package com.seeleaf.hotel.controller;

import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.dto.request.DepositCreateRequest;
import com.seeleaf.hotel.entity.Deposit;
import com.seeleaf.hotel.service.DepositService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 押金管理接口。
 * <p>
 * POST / — 收取押金<br>
 * GET / — 查询押金记录（可按入住记录筛选）<br>
 * POST /{id}/refund — 退还押金
 */
@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    /**
     * 收取押金。
     * <p>
     * 创建押金记录 → 累加到入住记录的 depositAmount 余额 → 写入账务流水。
     * 支持多种支付方式（现金/POS/微信/支付宝），对应 slipNumber / transactionNo / authCode。
     *
     * @param request 押金收取信息（入住记录、支付方式、金额）
     * @return 新建的押金记录
     */
    @PostMapping
    @PreAuthorize("hasAuthority('deposit:create')")
    public Result<Deposit> createDeposit(@Valid @RequestBody DepositCreateRequest request) {
        return Result.success(depositService.createDeposit(request));
    }

    /**
     * 查询押金记录。
     *
     * @param registrationId 可选，按入住记录筛选；为空返回全部
     * @return 押金记录列表（按创建时间倒序）
     */
    @GetMapping
    @PreAuthorize("hasAuthority('deposit:create')")
    public Result<List<Deposit>> queryDeposits(@RequestParam(required = false) Long registrationId) {
        return Result.success(depositService.queryDeposits(registrationId));
    }

    /**
     * 退还押金。
     * <p>
     * 标记押金记录为 refunded → 从入住记录的 depositAmount 中扣除该笔押金 → 写入退款流水。
     * 已退还的押金不可重复操作。
     *
     * @param id 押金记录 ID
     * @return 更新后的押金记录（status=refunded）
     */
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('deposit:create')")
    public Result<Deposit> refundDeposit(@PathVariable Long id) {
        return Result.success(depositService.refundDeposit(id));
    }
}
