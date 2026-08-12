package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.base.Sku;
import com.freshdelivery.mapper.base.SkuMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SkuService extends ServiceImpl<SkuMapper, Sku> {

    public Sku create(Sku sku) {
        sku.setStatus(sku.getStatus() == null ? 1 : sku.getStatus());
        sku.setCreatedAt(LocalDateTime.now());
        sku.setUpdatedAt(LocalDateTime.now());
        this.save(sku);
        return sku;
    }

    public void update(Long id, Sku sku) {
        if (this.getById(id) == null) throw new BusinessException("SKU不存在");
        sku.setId(id);
        sku.setUpdatedAt(LocalDateTime.now());
        this.updateById(sku);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("SKU不存在");
        this.removeById(id);
    }

    public Sku detail(Long id) {
        Sku sku = this.getById(id);
        if (sku == null) throw new BusinessException("SKU不存在");
        return sku;
    }

    public PageResult<Sku> page(int pageNum, int pageSize, String keyword, Long productId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Sku::getSpecName, keyword)
                    .or().like(Sku::getSpecValue, keyword));
        }
        if (productId != null) {
            wrapper.eq(Sku::getProductId, productId);
        }
        wrapper.orderByDesc(Sku::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<Sku> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}