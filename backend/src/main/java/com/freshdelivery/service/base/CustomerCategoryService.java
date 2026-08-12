package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.base.CustomerCategory;
import com.freshdelivery.mapper.base.CustomerCategoryMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerCategoryService extends ServiceImpl<CustomerCategoryMapper, CustomerCategory> {

    public CustomerCategory create(CustomerCategory category) {
        category.setStatus(category.getStatus() == null ? 1 : category.getStatus());
        this.save(category);
        return category;
    }

    public void update(Long id, CustomerCategory category) {
        if (this.getById(id) == null) throw new BusinessException("分类不存在");
        category.setId(id);
        this.updateById(category);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("分类不存在");
        this.removeById(id);
    }

    public List<CustomerCategory> list() {
        return this.list(new LambdaQueryWrapper<>(CustomerCategory.class)
                .eq(CustomerCategory::getStatus, 1)
                .orderByAsc(CustomerCategory::getId));
    }
}