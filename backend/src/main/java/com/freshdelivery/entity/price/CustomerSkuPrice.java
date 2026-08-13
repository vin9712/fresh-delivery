package com.freshdelivery.entity.price;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerSkuPrice {

    private Long skuId;

    private Long productId;

    private String productName;

    private String productUnit;

    private String productSpec;

    private BigDecimal price;

    private String remark;
}