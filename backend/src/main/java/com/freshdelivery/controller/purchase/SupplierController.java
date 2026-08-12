package com.freshdelivery.controller.purchase;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.entity.purchase.Supplier;
import com.freshdelivery.service.purchase.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/page")
    public ResponseEntity<Page<Supplier>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return ResponseEntity.ok(supplierService.page(pageNum, pageSize, keyword, status));
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@RequestBody Supplier supplier) {
        supplierService.save(supplier);
        return ResponseEntity.ok(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.updateById(supplier);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.removeById(id);
        return ResponseEntity.ok().build();
    }
}