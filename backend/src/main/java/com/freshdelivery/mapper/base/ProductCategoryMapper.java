package com.freshdelivery.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshdelivery.entity.base.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    @Select("SELECT IFNULL(MAX(sort), 0) + 1 FROM product_category WHERE parent_id = #{parentId} AND is_deleted = 0")
    Integer nextSort(Long parentId);
}