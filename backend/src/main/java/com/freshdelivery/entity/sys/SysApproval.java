package com.freshdelivery.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_approval")
public class SysApproval {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("biz_type")
    private String bizType;

    @TableField("biz_id")
    private Long bizId;

    @TableField("submit_user")
    private String submitUser;

    @TableField("approver")
    private String approver;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("submit_time")
    private LocalDateTime submitTime;

    @TableField("approve_time")
    private LocalDateTime approveTime;
}