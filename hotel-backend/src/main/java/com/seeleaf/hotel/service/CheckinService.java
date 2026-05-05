package com.seeleaf.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seeleaf.hotel.dto.request.CheckinRequest;
import com.seeleaf.hotel.dto.response.CheckinResponse;
import com.seeleaf.hotel.entity.Registration;

/**
 * 入住服务接口。
 * <p>核心入住流程：校验房间可用性 → 创建入住登记 → 更新房态为已入住 → 关联预订（如有）。
 * 支持入住记录的分页查询、详情查看和更新（含换房操作）。</p>
 */
public interface CheckinService {

    CheckinResponse createCheckin(CheckinRequest request);

    IPage<Registration> queryCheckins(int page, int pageSize, String keyword, String status);

    Registration getCheckinDetail(Long id);

    void updateCheckin(Long id, CheckinRequest request);
}
