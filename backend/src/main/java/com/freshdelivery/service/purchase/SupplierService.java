package com.freshdelivery.service.purchase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.purchase.Supplier;
import com.freshdelivery.mapper.purchase.SupplierMapper;
import org.springframework.stereotype.Service;

@Service
public class SupplierService extends ServiceImpl<SupplierMapper, Supplier> {

    public Page<Supplier> page(int pageNum, int pageSize, String keyword, Integer status) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>(Supplier.class);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Supplier::getName, keyword).or().like(Supplier::getContactPerson, keyword);
        }
        if (status != null) {
            wrapper.eq(Supplier::getStatus, status);
        }
        wrapper.orderByDesc(Supplier::getId);
        return this.page(page, wrapper);
    }
}