package com.seeleaf.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.common.response.PageResult;
import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.entity.Guest;
import com.seeleaf.hotel.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 客人管理接口。
 * <p>
 * GET / — 分页查询客人（支持姓名/电话/证件号关键词）<br>
 * GET /{id} — 客人详情<br>
 * POST / — 新建客人<br>
 * PUT /{id} — 更新客人信息
 */
@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    /**
     * 分页查询客人档案。
     * <p>
     * 支持按姓名、电话、证件号模糊搜索（三个字段 OR 匹配）。
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param keyword  搜索关键词（可选）
     * @return 客人档案分页列表
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('guest:query', 'guest:create')")
    public PageResult<Guest> queryGuests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<Guest> result = guestService.queryGuests(page, pageSize, keyword);
        return PageResult.success(result);
    }

    /**
     * 获取客人详情。
     *
     * @param id 客人 ID
     * @return 客人完整档案
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('guest:query', 'guest:create')")
    public Result<Guest> getGuestDetail(@PathVariable Long id) {
        return Result.success(guestService.getGuestDetail(id));
    }

    /**
     * 新建客人档案。
     *
     * @param guest 客人信息（推荐至少填写姓名、电话、证件号）
     * @return 新建的客人档案（含自增 ID）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('guest:create')")
    public Result<Guest> createGuest(@RequestBody Guest guest) {
        return Result.success(guestService.createGuest(guest));
    }

    /**
     * 更新客人档案。
     *
     * @param id    客人 ID
     * @param guest 要更新的字段（只传需要修改的字段即可）
     * @return 更新后的客人档案
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('guest:create')")
    public Result<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guest) {
        return Result.success(guestService.updateGuest(id, guest));
    }
}
