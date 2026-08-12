package com.freshdelivery.controller.order;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.order.Order;
import com.freshdelivery.entity.order.OrderItem;
import com.freshdelivery.service.order.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Data
    public static class CreateOrderRequest {
        private Order order;
        private List<OrderItem> items;
    }

    @GetMapping("/page")
    public Result<PageResult<Order>> page(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long customerId,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) LocalDate orderDate) {
        return Result.ok(orderService.page(pageNum, pageSize, keyword, customerId, status, orderDate));
    }

    @PostMapping
    @OperationLog(module = "order", action = "add")
    public Result<Order> create(@RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.create(request.getOrder(), request.getItems()));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.ok(orderService.detail(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        return Result.ok(orderService.findItems(id));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "order", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody Order order) {
        orderService.update(id, order);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "order", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/confirm")
    @OperationLog(module = "order", action = "confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.ok();
    }

    @PutMapping("/{id}/deliver")
    @OperationLog(module = "order", action = "deliver")
    public Result<Void> deliver(@PathVariable Long id) {
        orderService.deliver(id);
        return Result.ok();
    }

    @PutMapping("/{id}/accept")
    @OperationLog(module = "order", action = "accept")
    public Result<Void> accept(@PathVariable Long id) {
        orderService.accept(id);
        return Result.ok();
    }

    @PutMapping("/{id}/settle")
    @OperationLog(module = "order", action = "settle")
    public Result<Void> settle(@PathVariable Long id) {
        orderService.settle(id);
        return Result.ok();
    }
}