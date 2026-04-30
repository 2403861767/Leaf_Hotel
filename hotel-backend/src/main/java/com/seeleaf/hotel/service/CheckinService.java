package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.dto.request.CheckinRequest;
import com.seeleaf.hotel.dto.response.CheckinResponse;
import com.seeleaf.hotel.entity.Registration;

public interface CheckinService {

    CheckinResponse createCheckin(CheckinRequest request);

    IPage<Registration> queryCheckins(int page, int pageSize, String keyword, String status);

    Registration getCheckinDetail(Long id);

    void updateCheckin(Long id, CheckinRequest request);
}
