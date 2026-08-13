package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.base.ProductCategory;
import com.freshdelivery.mapper.base.ProductCategoryMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductCategoryService extends ServiceImpl<ProductCategoryMapper, ProductCategory> {

    @Autowired private ProductCategoryMapper categoryMapper;

    public ProductCategory detail(Long id) {
        ProductCategory category = this.getById(id);
        if (category == null) throw new BusinessException("分类不存在");
        return category;
    }

    public List<ProductCategory> list() {
        return this.list(new LambdaQueryWrapper<>(ProductCategory.class)
                .eq(ProductCategory::getIsDeleted, 0)
                .orderByAsc(ProductCategory::getSort));
    }

    @Transactional
    public ProductCategory create(ProductCategory category) {
        if (category.getParentId() == null) category.setParentId(0L);
        ProductCategory parent = (category.getParentId() == 0L) ? null : this.getById(category.getParentId());
        int parentLevel = (parent == null) ? 0 : (parent.getLevel() == null ? 0 : parent.getLevel());
        category.setLevel(parentLevel + 1);
        category.setSort(category.getSort() == null ? nextSort(category.getParentId()) : category.getSort());
        category.setIsDeleted(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        if (category.getCode() == null || category.getCode().isBlank()) {
            category.setCode(generateCode());
        }
        this.save(category);
        return this.getById(category.getId());
    }

    @Transactional
    public void update(ProductCategory category) {
        if (category.getId() == null) throw new BusinessException("分类ID不能为空");
        if (this.getById(category.getId()) == null) throw new BusinessException("分类不存在");
        // 不允许将自己设为自己的父节点
        if (category.getParentId() != null && category.getParentId().equals(category.getId())) {
            throw new BusinessException("不能将自己设为自己的父节点");
        }
        category.setUpdateTime(LocalDateTime.now());
        this.updateById(category);
    }

    @Transactional
    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("分类不存在");
        List<Long> ids = collectDescendants(id);
        if (!ids.isEmpty()) {
            this.removeByIds(ids);
        }
    }

    /** 递归收集某节点及其所有后代的 ID */
    private List<Long> collectDescendants(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<ProductCategory> children = this.list(
                new LambdaQueryWrapper<>(ProductCategory.class)
                        .eq(ProductCategory::getParentId, id)
                        .eq(ProductCategory::getIsDeleted, 0));
        for (ProductCategory child : children) {
            ids.addAll(collectDescendants(child.getId()));
        }
        return ids;
    }

    public Integer nextSort(Long parentId) {
        return categoryMapper.nextSort(parentId);
    }

    private String generateCode() {
        long count = this.count();
        return String.format("CAT%04d", count + 1);
    }
}