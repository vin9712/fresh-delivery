package com.freshdelivery.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("customer_sku_mapping")
public class CustomerSkuMapping {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("customer_name")
    private String customerName;

    @TableField("customer_alias")
    private String customerAlias;

    @TableField("en_short")
    private String enShort;

    @TableField("status")
    private Integer status;
}