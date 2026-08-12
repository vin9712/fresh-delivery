package com.freshdelivery.entity.delivery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("delivery_item")
public class DeliveryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("delivery_order_id")
    private Long deliveryOrderId;

    @TableField("origin_order_id")
    private Long originOrderId;

    @TableField("origin_item_id")
    private Long originItemId;

    @TableField("item_name")
    private String itemName;

    @TableField("item_spec")
    private String itemSpec;

    @TableField("item_unit")
    private String itemUnit;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("subtotal")
    private BigDecimal subtotal;

    @TableField("adjust_status")
    private Integer adjustStatus;

    @TableField("remark")
    private String remark;
}