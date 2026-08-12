package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.base.Customer;
import com.freshdelivery.mapper.base.CustomerMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {

    public Customer create(Customer customer) {
        customer.setStatus(customer.getStatus() == null ? 1 : customer.getStatus());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        this.save(customer);
        return customer;
    }

    public void update(Long id, Customer customer) {
        if (this.getById(id) == null) throw new BusinessException("客户不存在");
        customer.setId(id);
        customer.setUpdatedAt(LocalDateTime.now());
        this.updateById(customer);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("客户不存在");
        this.removeById(id);
    }

    public Customer detail(Long id) {
        Customer customer = this.getById(id);
        if (customer == null) throw new BusinessException("客户不存在");
        return customer;
    }

    public PageResult<Customer> page(int pageNum, int pageSize, String keyword, Long categoryId) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                    .or().like(Customer::getContactPerson, keyword)
                    .or().like(Customer::getPhone, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Customer::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Customer::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<Customer> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}