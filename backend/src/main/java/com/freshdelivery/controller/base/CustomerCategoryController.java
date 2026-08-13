package com.freshdelivery.controller.base;

import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.CustomerCategory;
import com.freshdelivery.service.base.CustomerCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/category")
@RequiredArgsConstructor
public class CustomerCategoryController {

    private final CustomerCategoryService categoryService;

    @GetMapping("/list")
    public Result<List<CustomerCategory>> list() {
        return Result.ok(categoryService.list());
    }

    @GetMapping("/listAll")
    public Result<List<CustomerCategory>> listAll() {
        return Result.ok(categoryService.listAll());
    }

    @PostMapping
    @OperationLog(module = "category", action = "add")
    public Result<Void> create(@RequestBody CustomerCategory category) {
        categoryService.create(category);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "category", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerCategory category) {
        categoryService.update(id, category);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "category", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}