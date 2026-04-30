package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.entity.Guest;

public interface GuestService {

    IPage<Guest> queryGuests(int page, int pageSize, String keyword);

    Guest getGuestDetail(Long id);

    Guest createGuest(Guest guest);

    Guest updateGuest(Long id, Guest guest);
}
