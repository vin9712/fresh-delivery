package com.freshdelivery.mapper.price;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshdelivery.entity.price.CustomerPrice;
import com.freshdelivery.entity.price.CustomerSkuPrice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CustomerPriceMapper extends BaseMapper<CustomerPrice> {

    @Select("SELECT p.id AS productId, s.id AS skuId, p.name AS productName, " +
            "s.unit AS productUnit, CONCAT_WS('/', s.spec_name, s.spec_value) AS productSpec, " +
            "cp.price, cp.remark " +
            "FROM customer_price cp " +
            "LEFT JOIN sku s ON cp.sku_id = s.id " +
            "LEFT JOIN product p ON s.product_id = p.id " +
            "WHERE cp.customer_id = #{customerId} AND cp.status = 1 " +
            "ORDER BY p.name, s.id")
    List<CustomerSkuPrice> listByCustomer(@Param("customerId") Long customerId);
}