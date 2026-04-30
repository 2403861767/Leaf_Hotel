package com.seeleaf.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.common.response.PageResult;
import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.dto.request.CheckoutRequest;
import com.seeleaf.hotel.dto.response.CheckoutResponse;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 退房结账接口。
 * <p>
 * POST / — 办理退房（计算房费 → 押金抵扣 → 退差额 → 房间标记 dirty）<br>
 * GET / — 分页查询已退房记录<br>
 * POST /calculate-refund — 预计算退款金额（退房前预览）
 */
@RestController
@RequestMapping("/api/v1/checkouts")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    /**
     * 办理退房结账。
     * <p>
     * 计算住宿天数和房费 → 叠加杂费 → 押金抵扣 → 有结余时自动退款 →
     * 入住记录标记为 checked_out → 客房标记为 dirty（待清洁）。
     *
     * @param request 退房请求（入住记录 ID + 可选杂费明细）
     * @return 结算明细（房费、杂费、总费用、押金、退款金额）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('checkout:create')")
    public Result<CheckoutResponse> createCheckout(@Valid @RequestBody CheckoutRequest request) {
        return Result.success(checkoutService.createCheckout(request));
    }

    /**
     * 分页查询已退房记录。
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词
     * @return 已退房的入住记录分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('checkin:query')")
    public PageResult<Registration> queryCheckouts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<Registration> result = checkoutService.queryCheckouts(page, pageSize, keyword);
        return PageResult.success(result);
    }

    /**
     * 预计算退款金额（退房前预览）。
     * <p>
     * 与实际退房逻辑一致计算房费和退款，但不修改数据库。供前台在退房确认弹窗中展示。
     *
     * @param registrationId 入住记录 ID
     * @return 预览的结算明细（含预估退款金额）
     */
    @PostMapping("/calculate-refund")
    @PreAuthorize("hasAuthority('checkout:create')")
    public Result<CheckoutResponse> calculateRefund(@RequestParam Long registrationId) {
        return Result.success(checkoutService.calculateRefund(registrationId));
    }
}
