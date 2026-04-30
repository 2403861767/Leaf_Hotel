package com.seeleaf.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.common.response.PageResult;
import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.dto.request.CheckinRequest;
import com.seeleaf.hotel.dto.response.CheckinResponse;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.service.CheckinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 入住登记接口。
 * <p>
 * POST / — 办理入住（校验房态 → 创建记录 → 房间标记 occupied）<br>
 * GET / — 分页查询入住记录<br>
 * GET /{id} — 入住详情<br>
 * PUT /{id} — 修改入住信息（含换房逻辑）
 */
@RestController
@RequestMapping("/api/v1/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    /**
     * 办理入住登记。
     * <p>
     * 校验客房为 available 状态 → 创建入住记录（status=in_house）→ 客房标记为 occupied。
     * 如有预订关联，需传入 reservationId。
     *
     * @param request 入住信息（客人、房间、预计离店日期、押金等）
     * @return 入住记录编号、房号、状态
     */
    @PostMapping
    @PreAuthorize("hasAuthority('checkin:create')")
    public Result<CheckinResponse> createCheckin(@Valid @RequestBody CheckinRequest request) {
        return Result.success(checkinService.createCheckin(request));
    }

    /**
     * 分页查询入住记录。
     *
     * @param page     页码（从 1 开始）
     * @param pageSize 每页条数
     * @param keyword  搜索关键词（匹配入住编号或房间号）
     * @param status   筛选状态（in_house/checked_out），为空则查询全部
     * @return 分页的入住记录列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('checkin:query')")
    public PageResult<Registration> queryCheckins(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        IPage<Registration> result = checkinService.queryCheckins(page, pageSize, keyword, status);
        return PageResult.success(result);
    }

    /**
     * 获取入住详情。
     *
     * @param id 入住记录 ID
     * @return 入住登记完整信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('checkin:query')")
    public Result<Registration> getCheckinDetail(@PathVariable Long id) {
        return Result.success(checkinService.getCheckinDetail(id));
    }

    /**
     * 修改入住信息（含换房）。
     * <p>
     * 支持修改预计离店日期、入住人数和房间。若变更房间（roomId），自动释放旧房
     * （标记为 available）并占用新房（标记为 occupied）。
     *
     * @param id      入住记录 ID
     * @param request 新的入住信息（只需传需要修改的字段）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('checkin:update')")
    public Result<Void> updateCheckin(@PathVariable Long id, @Valid @RequestBody CheckinRequest request) {
        checkinService.updateCheckin(id, request);
        return Result.success();
    }
}
