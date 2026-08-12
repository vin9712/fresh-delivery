package com.freshdelivery.entity.delivery;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("acceptance_item")
public class AcceptanceItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("acceptance_id")
    private Long acceptanceId;

    @TableField("delivery_item_id")
    private Long deliveryItemId;

    @TableField("item_name")
    private String itemName;

    @TableField("item_spec")
    private String itemSpec;

    @TableField("item_unit")
    private String itemUnit;

    @TableField("delivered_quantity")
    private BigDecimal deliveredQuantity;

    @TableField("actual_quantity")
    private BigDecimal actualQuantity;

    @TableField("loss_quantity")
    private BigDecimal lossQuantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("actual_amount")
    private BigDecimal actualAmount;

    @TableField("remark")
    private String remark;
}