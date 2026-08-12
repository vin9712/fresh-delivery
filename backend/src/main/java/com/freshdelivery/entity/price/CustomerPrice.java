package com.freshdelivery.entity.price;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("customer_price")
public class CustomerPrice {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("customer_id")
    private Long customerId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("price")
    private BigDecimal price;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("source_type")
    private Integer sourceType;

    @TableField("source_id")
    private Long sourceId;

    @TableField("status")
    private Integer status;
}