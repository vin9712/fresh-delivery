package com.freshdelivery.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("order_adjustment")
public class OrderAdjustment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("origin_item_id")
    private Long originItemId;

    @TableField("adjust_type")
    private Integer adjustType;

    @TableField("adjust_date")
    private LocalDate adjustDate;

    @TableField("order_date")
    private LocalDate orderDate;

    @TableField("item_name")
    private String itemName;

    @TableField("item_spec")
    private String itemSpec;

    @TableField("item_unit")
    private String itemUnit;

    @TableField("sku_id")
    private Long skuId;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("remark")
    private String remark;

    @TableField("created_by")
    private String createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;
}