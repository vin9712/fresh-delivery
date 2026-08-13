package com.freshdelivery.controller.price;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.price.CustomerPrice;
import com.freshdelivery.entity.price.CustomerSkuPrice;
import com.freshdelivery.service.price.CustomerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price/customer")
@RequiredArgsConstructor
public class CustomerPriceController {

    private final CustomerPriceService customerPriceService;

    @GetMapping("/page")
    public Result<PageResult<CustomerPrice>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(required = false) Long customerId,
                                                    @RequestParam(required = false) Integer status) {
        return Result.ok(customerPriceService.page(pageNum, pageSize, null, customerId, status));
    }

    @GetMapping("/sku-prices/{customerId}")
    public Result<List<CustomerSkuPrice>> listSkuPrices(@PathVariable Long customerId) {
        return Result.ok(customerPriceService.listSkuPrices(customerId));
    }

    @PostMapping
    @OperationLog(module = "customer_price", action = "add")
    public Result<Void> create(@RequestBody CustomerPrice price) {
        customerPriceService.create(price);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "customer_price", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerPrice price) {
        customerPriceService.update(id, price);
        return Result.ok();
    }

    @PostMapping("/import/{templateId}")
    @OperationLog(module = "customer_price", action = "import_template")
    public Result<Void> importFromTemplate(@PathVariable Long templateId,
                                            @RequestParam Long customerId) {
        customerPriceService.importFromTemplate(templateId, customerId);
        return Result.ok();
    }

    @PutMapping("/{id}/activate")
    @OperationLog(module = "customer_price", action = "activate")
    public Result<Void> activate(@PathVariable Long id) {
        customerPriceService.activate(id);
        return Result.ok();
    }

    @PutMapping("/{id}/reject")
    @OperationLog(module = "customer_price", action = "reject")
    public Result<Void> reject(@PathVariable Long id) {
        customerPriceService.reject(id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "customer_price", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        customerPriceService.delete(id);
        return Result.ok();
    }
}