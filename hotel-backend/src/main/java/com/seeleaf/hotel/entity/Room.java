package com.seeleaf.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("room")
public class Room {

    @TableId
    private Long id;

    private String roomNumber;

    private Integer floor;

    private Long roomTypeId;

    /** available(可售) / occupied(已入住) / dirty(待清洁) / maintenance(维修) */
    private String status;

    /** 乐观锁版本号，防止并发操作导致房态冲突 */
    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 非数据库字段：由 RoomServiceImpl.getRoomDetail 手动填充 */
    @TableField(exist = false)
    private RoomType roomType;
}
