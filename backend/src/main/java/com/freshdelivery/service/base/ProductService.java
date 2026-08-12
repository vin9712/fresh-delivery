package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.base.Product;
import com.freshdelivery.mapper.base.ProductMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

    @Transactional
    public Product create(Product product) {
        if (this.exists(new LambdaQueryWrapper<>(Product.class)
                .eq(Product::getName, product.getName()))) {
            throw new BusinessException("商品名称已存在");
        }
        product.setStatus(product.getStatus() == null ? 1 : product.getStatus());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        this.save(product);
        return product;
    }

    public void update(Long id, Product product) {
        if (this.getById(id) == null) throw new BusinessException("商品不存在");
        product.setId(id);
        product.setUpdatedAt(LocalDateTime.now());
        this.updateById(product);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("商品不存在");
        this.removeById(id);
    }

    public PageResult<Product> page(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getEnShort, keyword));
        }
        wrapper.orderByDesc(Product::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<Product> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    public Product detail(Long id) {
        Product product = this.getById(id);
        if (product == null) throw new BusinessException("商品不存在");
        return product;
    }
}