package com.seeleaf.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeleaf.hotel.common.exception.BusinessException;
import com.seeleaf.hotel.common.exception.ErrorCode;
import com.seeleaf.hotel.entity.Guest;
import com.seeleaf.hotel.mapper.GuestMapper;
import com.seeleaf.hotel.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 客人管理服务实现，提供客人信息的 CRUD 与关键词搜索。
 * <p>
 * 搜索支持姓名、电话、证件号三个字段的模糊匹配。
 */
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestMapper guestMapper;

    @Override
    public IPage<Guest> queryGuests(int page, int pageSize, String keyword) {
        Page<Guest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Guest> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Guest::getName, keyword)
                    .or().like(Guest::getPhone, keyword)
                    .or().like(Guest::getIdNumber, keyword);
        }
        wrapper.orderByDesc(Guest::getCreatedAt);
        return guestMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Guest getGuestDetail(Long id) {
        Guest guest = guestMapper.selectById(id);
        if (guest == null) {
            throw new BusinessException(ErrorCode.GUEST_NOT_FOUND);
        }
        return guest;
    }

    @Override
    public Guest createGuest(Guest guest) {
        guestMapper.insert(guest);
        return guest;
    }

    @Override
    public Guest updateGuest(Long id, Guest guest) {
        Guest existing = guestMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.GUEST_NOT_FOUND);
        }
        guest.setId(id);
        guestMapper.updateById(guest);
        return guestMapper.selectById(id);
    }
}
