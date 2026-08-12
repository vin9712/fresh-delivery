package com.freshdelivery.controller.base;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.DeliveryPoint;
import com.freshdelivery.service.base.DeliveryPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/delivery-point")
@RequiredArgsConstructor
public class DeliveryPointController {

    private final DeliveryPointService deliveryPointService;

    @GetMapping("/page")
    public Result<PageResult<DeliveryPoint>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Long customerId) {
        return Result.ok(deliveryPointService.page(pageNum, pageSize, keyword, customerId));
    }

    @GetMapping("/customer/{customerId}")
    public Result<List<DeliveryPoint>> listByCustomer(@PathVariable Long customerId) {
        return Result.ok(deliveryPointService.findByCustomerId(customerId));
    }

    @PostMapping
    @OperationLog(module = "delivery_point", action = "add")
    public Result<Void> create(@RequestBody DeliveryPoint point) {
        deliveryPointService.create(point);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "delivery_point", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody DeliveryPoint point) {
        deliveryPointService.update(id, point);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "delivery_point", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        deliveryPointService.delete(id);
        return Result.ok();
    }
}