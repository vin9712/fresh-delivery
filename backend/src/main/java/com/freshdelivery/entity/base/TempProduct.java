package com.freshdelivery.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("temp_product")
public class TempProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("name")
    private String name;

    @TableField("spec")
    private String spec;

    @TableField("unit")
    private String unit;

    @TableField("price")
    private BigDecimal price;

    @TableField("status")
    private Integer status;

    @TableField("formal_sku_id")
    private Long formalSkuId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}