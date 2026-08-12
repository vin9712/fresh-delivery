package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.base.ProductAlias;
import com.freshdelivery.mapper.base.ProductAliasMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAliasService extends ServiceImpl<ProductAliasMapper, ProductAlias> {

    public void create(ProductAlias alias) {
        this.save(alias);
    }

    public void update(Long id, ProductAlias alias) {
        alias.setId(id);
        this.updateById(alias);
    }

    public void delete(Long id) {
        this.removeById(id);
    }

    public List<ProductAlias> findByProductId(Long productId) {
        return this.list(new LambdaQueryWrapper<>(ProductAlias.class)
                .eq(ProductAlias::getProductId, productId));
    }
}