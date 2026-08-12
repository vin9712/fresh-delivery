package com.freshdelivery.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("product_alias")
public class ProductAlias {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("alias")
    private String alias;

    @TableField("en_short")
    private String enShort;
}