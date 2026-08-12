package com.freshdelivery.controller.delivery;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.controller.delivery.CreateDeliveryOrderRequest;
import com.freshdelivery.entity.delivery.DeliveryOrder;
import com.freshdelivery.entity.delivery.DeliveryItem;
import com.freshdelivery.service.delivery.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/delivery/order")
public class DeliveryOrderController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @GetMapping("/page")
    public ResponseEntity<Page<DeliveryOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(deliveryOrderService.page(pageNum, pageSize, keyword, customerId, status, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<DeliveryOrder> create(@RequestBody CreateDeliveryOrderRequest req) {
        return ResponseEntity.ok(deliveryOrderService.create(req.order(), req.items()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryOrder> detail(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryOrderService.detail(id));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<DeliveryItem>> items(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryOrderService.findItems(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deliveryOrderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<Void> markDelivered(@PathVariable Long id) {
        deliveryOrderService.markDelivered(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Void> markAccepted(@PathVariable Long id) {
        deliveryOrderService.markAccepted(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/print")
    public ResponseEntity<DeliveryOrder> printDelivery(@PathVariable Long id) {
        deliveryOrderService.printDelivery(id);
        return ResponseEntity.ok(deliveryOrderService.detail(id));
    }
}