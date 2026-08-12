package com.freshdelivery.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("permission_key")
    private String permissionKey;

    @TableField("module")
    private String module;

    @TableField("description")
    private String description;
}