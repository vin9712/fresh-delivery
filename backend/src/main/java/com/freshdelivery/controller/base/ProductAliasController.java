package com.freshdelivery.controller.base;

import com.freshdelivery.common.Result;
import com.freshdelivery.entity.base.ProductAlias;
import com.freshdelivery.service.base.ProductAliasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/alias")
@RequiredArgsConstructor
public class ProductAliasController {

    private final ProductAliasService aliasService;

    @GetMapping("/product/{productId}")
    public Result<List<ProductAlias>> listByProduct(@PathVariable Long productId) {
        return Result.ok(aliasService.findByProductId(productId));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ProductAlias alias) {
        aliasService.create(alias);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductAlias alias) {
        aliasService.update(id, alias);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        aliasService.delete(id);
        return Result.ok();
    }
}