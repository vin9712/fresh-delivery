package com.freshdelivery.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("product_id")
    private Long productId;

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

    @TableField("item_status")
    private Integer itemStatus;

    @TableField("remark")
    private String remark;

    @TableField("created_at")
    private LocalDateTime createdAt;
}