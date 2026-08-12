package com.freshdelivery.entity.purchase;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("source_type")
    private Integer sourceType;

    @TableField("source_order_ids")
    private String sourceOrderIds;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private Integer status;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;
}