package com.freshdelivery.controller.base;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.Sku;
import com.freshdelivery.service.base.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping("/page")
    public Result<PageResult<Sku>> page(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long productId) {
        return Result.ok(skuService.page(pageNum, pageSize, keyword, productId));
    }

    @GetMapping("/{id}")
    public Result<Sku> detail(@PathVariable Long id) {
        return Result.ok(skuService.detail(id));
    }

    @PostMapping
    @OperationLog(module = "sku", action = "add")
    public Result<Void> create(@RequestBody Sku sku) {
        skuService.create(sku);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "sku", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody Sku sku) {
        skuService.update(id, sku);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "sku", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        skuService.delete(id);
        return Result.ok();
    }
}