package com.seeleaf.hotel.service;

import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.entity.RoomType;

import java.util.List;
import java.util.Map;

/**
 * 房间管理服务接口。
 * <p>提供房态面板所需的全部数据：按房态分组的状态地图、房间详情（含房型信息）、可用房间筛选。
 * 可选按房型过滤可用房间，用于入住时的房间选择列表。</p>
 */
public interface RoomService {

    Map<String, List<Room>> getStatusMap();

    Room getRoomDetail(Long id);

    List<Room> getAvailableRooms(Long roomTypeId);
}
