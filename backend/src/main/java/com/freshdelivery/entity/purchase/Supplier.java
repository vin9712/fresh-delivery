package com.freshdelivery.entity.purchase;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("supplier")
public class Supplier {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("contact_person")
    private String contactPerson;

    @TableField("phone")
    private String phone;

    @TableField("address")
    private String address;

    @TableField("is_default")
    private Integer isDefault;

    @TableField("status")
    private Integer status;
}