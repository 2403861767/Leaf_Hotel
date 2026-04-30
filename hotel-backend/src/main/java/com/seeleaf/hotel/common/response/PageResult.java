package com.seeleaf.hotel.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 统一分页响应格式。
 * <p>
 * 与 {@link Result} 结构一致，但 data 为包含分页信息的 PageData。
 * 前端可通过 data.list/total/page/pageSize 获取分页数据。
 */
@Data
public class PageResult<T> {

    private int code;
    private String message;
    private PageData<T> data;

    private PageResult() {}

    public static <T> PageResult<T> success(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.code = 200;
        result.message = "success";
        PageData<T> pageData = new PageData<>();
        pageData.setList(page.getRecords());
        pageData.setTotal(page.getTotal());
        pageData.setPage((int) page.getCurrent());
        pageData.setPageSize((int) page.getSize());
        result.data = pageData;
        return result;
    }

    @Data
    static class PageData<T> {
        private List<T> list;
        private long total;
        private int page;
        private int pageSize;
    }
}
