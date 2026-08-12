package com.freshdelivery.entity.purchase;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("product_supplier")
public class ProductSupplier {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("is_primary")
    private Integer isPrimary;
}