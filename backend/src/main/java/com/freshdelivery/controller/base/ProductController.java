package com.freshdelivery.controller.base;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.Product;
import com.freshdelivery.service.base.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String keyword) {
        return Result.ok(productService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }

    @PostMapping
    @OperationLog(module = "product", action = "add")
    public Result<Void> create(@RequestBody Product product) {
        productService.create(product);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "product", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        productService.update(id, product);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "product", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }
}