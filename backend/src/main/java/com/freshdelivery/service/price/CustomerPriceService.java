package com.freshdelivery.service.price;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.price.CustomerPrice;
import com.freshdelivery.entity.price.CustomerSkuPrice;
import com.freshdelivery.entity.price.PriceTemplateSku;
import com.freshdelivery.mapper.price.CustomerPriceMapper;
import com.freshdelivery.mapper.price.PriceTemplateSkuMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerPriceService extends ServiceImpl<CustomerPriceMapper, CustomerPrice> {

    @Autowired private PriceTemplateSkuMapper templateSkuMapper;
    @Autowired private CustomerPriceMapper customerPriceMapper;

    public CustomerPrice create(CustomerPrice price) {
        this.save(price);
        return price;
    }

    public void update(Long id, CustomerPrice price) {
        if (this.getById(id) == null) throw new BusinessException("报价不存在");
        price.setId(id);
        this.updateById(price);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("报价不存在");
        this.removeById(id);
    }

    public PageResult<CustomerPrice> page(int pageNum, int pageSize, String keyword, Long customerId, Integer status) {
        LambdaQueryWrapper<CustomerPrice> wrapper = new LambdaQueryWrapper<>();
        if (customerId != null) {
            wrapper.eq(CustomerPrice::getCustomerId, customerId);
        }
        if (status != null) {
            wrapper.eq(CustomerPrice::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(CustomerPrice::getCustomerId, keyword);
        }
        wrapper.orderByDesc(CustomerPrice::getId);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<CustomerPrice> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Transactional
    public List<CustomerPrice> importFromTemplate(Long templateId, Long customerId) {
        List<PriceTemplateSku> templateSkus = templateSkuMapper.selectList(
                new LambdaQueryWrapper<>(PriceTemplateSku.class)
                        .eq(PriceTemplateSku::getTemplateId, templateId));
        if (templateSkus.isEmpty()) {
            throw new BusinessException("该模板下无SKU报价");
        }
        List<CustomerPrice> prices = new java.util.ArrayList<>();
        for (PriceTemplateSku sku : templateSkus) {
            CustomerPrice price = new CustomerPrice();
            price.setCustomerId(customerId);
            price.setSkuId(sku.getSkuId());
            price.setPrice(sku.getPrice());
            price.setStartDate(sku.getStartDate());
            price.setEndDate(sku.getEndDate());
            price.setSourceType(1);
            price.setSourceId(templateId);
            price.setStatus(0);
            this.save(price);
            prices.add(price);
        }
        return prices;
    }

    public void activate(Long id) {
        CustomerPrice price = this.getById(id);
        if (price == null) throw new BusinessException("报价不存在");
        price.setStatus(1);
        this.updateById(price);
    }

    public void reject(Long id) {
        CustomerPrice price = this.getById(id);
        if (price == null) throw new BusinessException("报价不存在");
        price.setStatus(2);
        this.updateById(price);
    }

    public List<CustomerSkuPrice> listSkuPrices(Long customerId) {
        return customerPriceMapper.listByCustomer(customerId);
    }
}