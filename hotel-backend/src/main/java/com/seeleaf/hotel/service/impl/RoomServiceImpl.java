package com.seeleaf.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seeleaf.hotel.common.constant.Constants;
import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.entity.Guest;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.entity.RoomType;
import com.seeleaf.hotel.mapper.GuestMapper;
import com.seeleaf.hotel.mapper.RegistrationMapper;
import com.seeleaf.hotel.mapper.RoomMapper;
import com.seeleaf.hotel.mapper.RoomTypeMapper;
import com.seeleaf.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客房管理服务实现，提供房态图、客房详情与可售房查询。
 * <p>
 * getStatusMap 返回按状态（available/occupied/dirty/maintenance）分组的 Map，
 * 供前台房态图页面展示。
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final RegistrationMapper registrationMapper;
    private final GuestMapper guestMapper;

    @Override
    public Map<String, List<Room>> getStatusMap() {
        List<Room> rooms = roomMapper.selectList(null);
        List<Long> typeIds = rooms.stream().map(Room::getRoomTypeId).distinct().collect(Collectors.toList());
        Map<Long, RoomType> typeMap = roomTypeMapper.selectBatchIds(typeIds).stream()
                .collect(Collectors.toMap(RoomType::getId, t -> t));
        for (Room room : rooms) {
            room.setRoomType(typeMap.get(room.getRoomTypeId()));
        }
        return rooms.stream().collect(Collectors.groupingBy(Room::getStatus));
    }

    @Override
    public Room getRoomDetail(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(ErrorCode.ROOM_NOT_FOUND);
        }
        RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
        room.setRoomType(roomType);

        if (Constants.ROOM_STATUS_OCCUPIED.equals(room.getStatus())) {
            LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Registration::getRoomId, id)
                    .eq(Registration::getStatus, Constants.REG_STATUS_IN_HOUSE);
            Registration registration = registrationMapper.selectOne(wrapper);
            if (registration != null) {
                Guest guest = guestMapper.selectById(registration.getGuestId());
                Room.OccupiedInfo info = new Room.OccupiedInfo();
                info.setGuestName(guest != null ? guest.getName() : null);
                info.setCheckInTime(registration.getCheckInTime());
                info.setExpectedCheckOut(registration.getExpectedCheckOutTime());
                room.setOccupiedInfo(info);
            }
        }

        return room;
    }

    @Override
    public List<Room> getAvailableRooms(Long roomTypeId) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getStatus, Constants.ROOM_STATUS_AVAILABLE);
        if (roomTypeId != null) {
            wrapper.eq(Room::getRoomTypeId, roomTypeId);
        }
        return roomMapper.selectList(wrapper);
    }
}
