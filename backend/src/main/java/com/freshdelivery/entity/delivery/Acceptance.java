package com.freshdelivery.entity.delivery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("acceptance")
public class Acceptance {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("delivery_order_id")
    private Long deliveryOrderId;

    @TableField("delivery_order_no")
    private String deliveryOrderNo;

    @TableField("delivery_date")
    private LocalDate deliveryDate;

    @TableField("customer_id")
    private Long customerId;

    @TableField("point_id")
    private Long pointId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("total_loss_amount")
    private BigDecimal totalLossAmount;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;
}