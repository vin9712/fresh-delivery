package com.freshdelivery.controller.purchase;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.common.Result;
import com.freshdelivery.entity.purchase.Supplier;
import com.freshdelivery.service.purchase.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/page")
    public Result<Page<Supplier>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.ok(supplierService.page(pageNum, pageSize, keyword, status));
    }

    @PostMapping
    public Result<Supplier> create(@RequestBody Supplier supplier) {
        supplierService.save(supplier);
        return Result.ok(supplier);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.updateById(supplier);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.removeById(id);
        return Result.ok();
    }
}
