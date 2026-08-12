package com.freshdelivery.controller.order;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.order.OrderAdjustment;
import com.freshdelivery.service.order.OrderAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/order/adjustment")
@RequiredArgsConstructor
public class OrderAdjustmentController {

    private final OrderAdjustmentService adjustmentService;

    @GetMapping("/page")
    public Result<PageResult<OrderAdjustment>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                     @RequestParam(required = false) Long orderId,
                                                     @RequestParam(required = false) Integer adjustType,
                                                     @RequestParam(required = false) LocalDate orderDate) {
        return Result.ok(adjustmentService.page(pageNum, pageSize, orderId, adjustType, orderDate));
    }

    @PostMapping
    @OperationLog(module = "order_adjustment", action = "add")
    public Result<OrderAdjustment> create(@RequestBody OrderAdjustment adjustment) {
        return Result.ok(adjustmentService.create(adjustment));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "order_adjustment", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        adjustmentService.delete(id);
        return Result.ok();
    }
}