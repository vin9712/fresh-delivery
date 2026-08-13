package com.freshdelivery.controller.base;

import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.ProductCategory;
import com.freshdelivery.service.base.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping("/list")
    public Result<List<ProductCategory>> list() {
        return Result.ok(categoryService.list());
    }

    @GetMapping("/{id}")
    public Result<ProductCategory> detail(@PathVariable Long id) {
        return Result.ok(categoryService.detail(id));
    }

    @GetMapping("/nextSort/{parentId}")
    public Result<Integer> nextSort(@PathVariable Long parentId) {
        return Result.ok(categoryService.nextSort(parentId));
    }

    @PostMapping
    @OperationLog(module = "product_category", action = "add")
    public Result<ProductCategory> create(@RequestBody ProductCategory category) {
        return Result.ok(categoryService.create(category));
    }

    @PutMapping
    @OperationLog(module = "product_category", action = "edit")
    public Result<Void> update(@RequestBody ProductCategory category) {
        categoryService.update(category);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "product_category", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}