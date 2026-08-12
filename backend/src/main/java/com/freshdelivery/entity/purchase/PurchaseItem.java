package com.freshdelivery.entity.purchase;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchase_item")
public class PurchaseItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("sku_id")
    private Long skuId;

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

    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;
}