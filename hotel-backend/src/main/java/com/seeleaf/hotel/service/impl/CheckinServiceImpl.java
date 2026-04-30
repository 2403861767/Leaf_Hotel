package com.seeleaf.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.dto.request.CheckinRequest;
import com.seeleaf.hotel.dto.response.CheckinResponse;
import com.seeleaf.hotel.entity.Registration;
import com.seeleaf.hotel.entity.Room;
import com.seeleaf.hotel.mapper.RegistrationMapper;
import com.seeleaf.hotel.mapper.RoomMapper;
import com.seeleaf.hotel.security.model.LoginUser;
import com.seeleaf.hotel.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 入住登记服务实现，处理客人办理入住、换房及入住记录查询等前台核心操作。
 * <p>
 * 入住时会校验客房状态，成功后创建入住记录并将客房标记为 occupied（已入住）。
 */
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final RegistrationMapper registrationMapper;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public CheckinResponse createCheckin(CheckinRequest request) {
        // 1. 校验客房是否存在且可售
        Room room = roomMapper.selectById(request.getRoomId());
        if (room == null) {
            throw new BusinessException(ErrorCode.ROOM_NOT_FOUND);
        }
        if (!"available".equals(room.getStatus())) {
            throw new BusinessException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 2. 创建入住登记记录，初始状态为 in_house（在住）
        Registration registration = new Registration();
        registration.setReservationId(request.getReservationId());
        registration.setGuestId(request.getGuestId());
        registration.setRoomId(request.getRoomId());
        registration.setCheckInTime(LocalDateTime.now());
        registration.setExpectedCheckOutTime(request.getExpectedCheckOutDate().atStartOfDay());
        registration.setGuestCount(request.getGuestCount());
        registration.setDepositAmount(request.getDepositAmount());
        registration.setOperatorId(loginUser.getUserId());
        registration.setSource(request.getSource());
        registration.setStatus("in_house");
        registrationMapper.insert(registration);

        // 3. 客房状态同步变更为 occupied（已入住）
        room.setStatus("occupied");
        roomMapper.updateById(room);

        CheckinResponse response = new CheckinResponse();
        response.setRegistrationId(registration.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setStatus("in_house");
        response.setCreatedAt(registration.getCreatedAt());
        return response;
    }

    @Override
    public IPage<Registration> queryCheckins(int page, int pageSize, String keyword, String status) {
        Page<Registration> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Registration::getId, keyword)
                    .or().eq(Registration::getRoomId, keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByDesc(Registration::getCreatedAt);
        return registrationMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Registration getCheckinDetail(Long id) {
        Registration registration = registrationMapper.selectById(id);
        if (registration == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
        }
        return registration;
    }

    @Override
    @Transactional
    public void updateCheckin(Long id, CheckinRequest request) {
        Registration registration = registrationMapper.selectById(id);
        if (registration == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_FOUND);
        }

        if (request.getExpectedCheckOutDate() != null) {
            registration.setExpectedCheckOutTime(request.getExpectedCheckOutDate().atStartOfDay());
        }
        if (request.getGuestCount() != null) {
            registration.setGuestCount(request.getGuestCount());
        }
        // 换房操作：释放旧房（available）→ 占用新房（occupied）
        if (request.getRoomId() != null && !request.getRoomId().equals(registration.getRoomId())) {
            Room newRoom = roomMapper.selectById(request.getRoomId());
            if (newRoom == null || !"available".equals(newRoom.getStatus())) {
                throw new BusinessException(ErrorCode.ROOM_NOT_AVAILABLE);
            }
            Room oldRoom = roomMapper.selectById(registration.getRoomId());
            if (oldRoom != null) {
                oldRoom.setStatus("available");
                roomMapper.updateById(oldRoom);
            }
            newRoom.setStatus("occupied");
            roomMapper.updateById(newRoom);
            registration.setRoomId(request.getRoomId());
        }
        registrationMapper.updateById(registration);
    }
}
