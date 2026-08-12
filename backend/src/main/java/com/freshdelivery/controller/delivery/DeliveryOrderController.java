package com.freshdelivery.controller.delivery;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.common.Result;
import com.freshdelivery.controller.delivery.CreateDeliveryOrderRequest;
import com.freshdelivery.entity.delivery.DeliveryOrder;
import com.freshdelivery.entity.delivery.DeliveryItem;
import com.freshdelivery.service.delivery.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/delivery/order")
public class DeliveryOrderController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @GetMapping("/page")
    public Result<Page<DeliveryOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(deliveryOrderService.page(pageNum, pageSize, keyword, customerId, status, startDate, endDate));
    }

    @PostMapping
    public Result<DeliveryOrder> create(@RequestBody CreateDeliveryOrderRequest req) {
        return Result.ok(deliveryOrderService.create(req.order(), req.items()));
    }

    @GetMapping("/{id}")
    public Result<DeliveryOrder> detail(@PathVariable Long id) {
        return Result.ok(deliveryOrderService.detail(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<DeliveryItem>> items(@PathVariable Long id) {
        return Result.ok(deliveryOrderService.findItems(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deliveryOrderService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/deliver")
    public Result<Void> markDelivered(@PathVariable Long id) {
        deliveryOrderService.markDelivered(id);
        return Result.ok();
    }

    @PutMapping("/{id}/accept")
    public Result<Void> markAccepted(@PathVariable Long id) {
        deliveryOrderService.markAccepted(id);
        return Result.ok();
    }

    @PutMapping("/{id}/print")
    public Result<DeliveryOrder> printDelivery(@PathVariable Long id) {
        deliveryOrderService.printDelivery(id);
        return Result.ok(deliveryOrderService.detail(id));
    }
}
