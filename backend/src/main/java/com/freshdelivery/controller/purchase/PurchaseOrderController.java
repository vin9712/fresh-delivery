package com.freshdelivery.controller.purchase;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.common.Result;
import com.freshdelivery.entity.purchase.PurchaseItem;
import com.freshdelivery.entity.purchase.PurchaseOrder;
import com.freshdelivery.service.purchase.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/purchase/order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/page")
    public Result<Page<PurchaseOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(purchaseOrderService.page(pageNum, pageSize, keyword, supplierId, status, startDate, endDate));
    }

    @PostMapping
    public Result<PurchaseOrder> create(@RequestBody CreatePurchaseOrderRequest req) {
        return Result.ok(purchaseOrderService.create(req.order(), req.items()));
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrder> detail(@PathVariable Long id) {
        return Result.ok(purchaseOrderService.detail(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<PurchaseItem>> items(@PathVariable Long id) {
        return Result.ok(purchaseOrderService.findItems(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        purchaseOrderService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/{id}/stock-in")
    public Result<Void> stockIn(@PathVariable Long id) {
        purchaseOrderService.stockIn(id);
        return Result.ok();
    }

    @GetMapping("/aggregate")
    public Result<List<PurchaseItem>> aggregate(@RequestParam List<Long> orderIds) {
        return Result.ok(purchaseOrderService.aggregateFromOrders(orderIds));
    }
}
