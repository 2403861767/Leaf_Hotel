package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.entity.Guest;

/**
 * 客人管理服务接口。
 * <p>负责客人档案的增删改查。关键词搜索采用三字段 OR 匹配（姓名、证件号、手机号），
 * 支持分页查询。每位客人由证件号唯一标识。</p>
 */
public interface GuestService {

    IPage<Guest> queryGuests(int page, int pageSize, String keyword);

    Guest getGuestDetail(Long id);

    Guest createGuest(Guest guest);

    Guest updateGuest(Long id, Guest guest);
}
