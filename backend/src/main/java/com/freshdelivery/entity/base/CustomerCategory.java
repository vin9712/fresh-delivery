package com.freshdelivery.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("customer_category")
public class CustomerCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("status")
    private Integer status;
}