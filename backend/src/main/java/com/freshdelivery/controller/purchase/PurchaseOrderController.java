package com.freshdelivery.controller.purchase;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.entity.purchase.PurchaseItem;
import com.freshdelivery.entity.purchase.PurchaseOrder;
import com.freshdelivery.service.purchase.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/purchase/order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/page")
    public ResponseEntity<Page<PurchaseOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(purchaseOrderService.page(pageNum, pageSize, keyword, supplierId, status, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(@RequestBody CreatePurchaseOrderRequest req) {
        return ResponseEntity.ok(purchaseOrderService.create(req.order(), req.items()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> detail(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.detail(id));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<PurchaseItem>> items(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.findItems(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        purchaseOrderService.confirm(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/stock-in")
    public ResponseEntity<Void> stockIn(@PathVariable Long id) {
        purchaseOrderService.stockIn(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/aggregate")
    public ResponseEntity<List<PurchaseItem>> aggregate(@RequestParam List<Long> orderIds) {
        return ResponseEntity.ok(purchaseOrderService.aggregateFromOrders(orderIds));
    }
}