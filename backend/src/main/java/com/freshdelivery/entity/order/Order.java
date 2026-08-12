package com.freshdelivery.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("customer_id")
    private Long customerId;

    @TableField("point_id")
    private Long pointId;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}