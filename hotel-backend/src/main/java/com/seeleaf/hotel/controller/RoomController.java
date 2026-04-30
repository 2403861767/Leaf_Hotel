package com.seeleaf.hotel.controller;

import com.seeleaf.hotel.common.response.Result;
import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客房管理接口。
 * <p>
 * GET /status-map — 房态图（按状态分组的房间列表）<br>
 * GET /{id} — 客房详情<br>
 * GET /available — 查询可售客房（可按房型筛选）
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 获取房态图。
     * <p>
     * 按状态（available/occupied/dirty/maintenance）分组返回所有客房列表，
     * 供前台房态图页面以不同颜色区块展示。
     *
     * @return 按状态分组的客房 Map
     */
    @GetMapping("/status-map")
    @PreAuthorize("hasAuthority('room:view')")
    public Result<Map<String, List<Room>>> getStatusMap() {
        return Result.success(roomService.getStatusMap());
    }

    /**
     * 获取客房详情。
     * <p>
     * 返回客房信息及其关联的房型信息（类型名称、基准价格等）。
     *
     * @param id 客房 ID
     * @return 客房详情（含 RoomType）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('room:view')")
    public Result<Room> getRoomDetail(@PathVariable Long id) {
        return Result.success(roomService.getRoomDetail(id));
    }

    /**
     * 查询当前可售客房。
     * <p>
     * 返回 status=available 的客房列表，可按房型筛选。用于办理入住时选择房间。
     *
     * @param roomTypeId 可选，按房型筛选
     * @return 可售客房列表
     */
    @GetMapping("/available")
    @PreAuthorize("hasAuthority('room:view')")
    public Result<List<Room>> getAvailableRooms(@RequestParam(required = false) Long roomTypeId) {
        return Result.success(roomService.getAvailableRooms(roomTypeId));
    }
}
