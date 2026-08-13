package com.freshdelivery.controller.base;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.base.Customer;
import com.freshdelivery.service.base.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/page")
    public Result<PageResult<Customer>> page(@RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Long categoryId) {
        return Result.ok(customerService.page(pageNum, pageSize, keyword, categoryId));
    }

    @GetMapping("/pageFilter")
    public Result<PageResult<Customer>> pageByFilter(@RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) Integer status) {
        return Result.ok(customerService.pageByFilter(pageNum, pageSize, name, status));
    }

    @GetMapping("/list")
    public Result<java.util.List<Customer>> list() {
        return Result.ok(customerService.list());
    }

    @GetMapping("/{id}")
    public Result<Customer> detail(@PathVariable Long id) {
        return Result.ok(customerService.detail(id));
    }

    @PostMapping
    @OperationLog(module = "customer", action = "add")
    public Result<Void> create(@RequestBody Customer customer) {
        customerService.create(customer);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "customer", action = "edit")
    public Result<Void> update(@PathVariable Long id, @RequestBody Customer customer) {
        customerService.update(id, customer);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "customer", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.ok();
    }
}