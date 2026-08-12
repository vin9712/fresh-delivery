package com.freshdelivery.controller.price;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.price.PriceTemplate;
import com.freshdelivery.entity.price.PriceTemplateSku;
import com.freshdelivery.service.price.PriceTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price/template")
@RequiredArgsConstructor
public class PriceTemplateController {

    private final PriceTemplateService templateService;

    @GetMapping("/page")
    public Result<PageResult<PriceTemplate>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(required = false) String keyword) {
        return Result.ok(templateService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<PriceTemplate> detail(@PathVariable Long id) {
        return Result.ok(templateService.detail(id));
    }

    @GetMapping("/{id}/skus")
    public Result<List<PriceTemplateSku>> skus(@PathVariable Long id) {
        return Result.ok(templateService.findSkuList(id));
    }

    @PostMapping
    @OperationLog(module = "price_template", action = "add")
    public Result<Void> create(@RequestBody PriceTemplate template) {
        templateService.create(template);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "price_template", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody PriceTemplate template) {
        templateService.update(id, template);
        return Result.ok();
    }

    @PutMapping("/{id}/skus")
    @OperationLog(module = "price_template", action = "save_skus")
    public Result<Void> saveSkus(@PathVariable Long id, @RequestBody List<PriceTemplateSku> skuList) {
        templateService.saveSkuList(id, skuList);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "price_template", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.ok();
    }
}