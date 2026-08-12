package com.freshdelivery.entity.delivery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("delivery_order")
public class DeliveryOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("delivery_date")
    private LocalDate deliveryDate;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("customer_id")
    private Long customerId;

    @TableField("point_id")
    private Long pointId;

    @TableField("total_quantity")
    private BigDecimal totalQuantity;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("template_id")
    private Long templateId;

    @TableField("print_count")
    private Integer printCount;

    @TableField("status")
    private Integer status;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;
}