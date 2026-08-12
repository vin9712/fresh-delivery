package com.freshdelivery.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshdelivery.entity.price.CustomerPrice;
import com.freshdelivery.entity.price.PriceTemplateSku;
import com.freshdelivery.mapper.price.CustomerPriceMapper;
import com.freshdelivery.mapper.price.PriceTemplateSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PriceLookupService {

    @Autowired private CustomerPriceMapper customerPriceMapper;
    @Autowired private PriceTemplateSkuMapper templateSkuMapper;

    public BigDecimal getPrice(Long customerId, Long skuId, LocalDate orderDate) {
        if (skuId == null) return null;

        CustomerPrice price = findInRange(customerId, skuId, orderDate);
        if (price != null) return price.getPrice();

        LocalDate today = LocalDate.now();
        price = findExpiring(customerId, skuId, today);
        if (price != null) return price.getPrice();

        PriceTemplateSku templateSku = templateSkuMapper.selectOne(
                new LambdaQueryWrapper<>(PriceTemplateSku.class)
                        .eq(PriceTemplateSku::getSkuId, skuId)
                        .orderByDesc(PriceTemplateSku::getStartDate)
                        .last("limit 1"));
        if (templateSku != null) return templateSku.getPrice();

        return null;
    }

    private CustomerPrice findInRange(Long customerId, Long skuId, LocalDate orderDate) {
        return customerPriceMapper.selectOne(
                new LambdaQueryWrapper<>(CustomerPrice.class)
                        .eq(CustomerPrice::getCustomerId, customerId)
                        .eq(CustomerPrice::getSkuId, skuId)
                        .eq(CustomerPrice::getStatus, 1)
                        .le(CustomerPrice::getStartDate, orderDate)
                        .ge(CustomerPrice::getEndDate, orderDate)
                        .orderByDesc(CustomerPrice::getId)
                        .last("limit 1"));
    }

    private CustomerPrice findExpiring(Long customerId, Long skuId, LocalDate today) {
        return customerPriceMapper.selectOne(
                new LambdaQueryWrapper<>(CustomerPrice.class)
                        .eq(CustomerPrice::getCustomerId, customerId)
                        .eq(CustomerPrice::getSkuId, skuId)
                        .eq(CustomerPrice::getStatus, 1)
                        .lt(CustomerPrice::getEndDate, today)
                        .orderByDesc(CustomerPrice::getStartDate)
                        .last("limit 1"));
    }
}