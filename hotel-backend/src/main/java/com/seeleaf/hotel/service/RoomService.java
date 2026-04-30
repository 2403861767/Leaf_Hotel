package com.seeleaf.hotel.service;

import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.entity.RoomType;

import java.util.List;
import java.util.Map;

public interface RoomService {

    Map<String, List<Room>> getStatusMap();

    Room getRoomDetail(Long id);

    List<Room> getAvailableRooms(Long roomTypeId);
}
