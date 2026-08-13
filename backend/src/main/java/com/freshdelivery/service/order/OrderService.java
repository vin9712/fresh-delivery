package com.freshdelivery.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.exception.BusinessException;
import com.freshdelivery.entity.order.Order;
import com.freshdelivery.entity.order.OrderItem;
import com.freshdelivery.mapper.order.OrderMapper;
import com.freshdelivery.mapper.order.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired private OrderItemMapper itemMapper;

    private static final DateTimeFormatter ORDER_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public Order create(Order order, List<OrderItem> items) {
        order.setStatus(0);
        String orderNo = "XD" + LocalDate.now().format(ORDER_NO_FMT) + String.format("%04d", nextNo());
        order.setOrderNo(orderNo);
        order.setCreatedAt(LocalDateTime.now());
        this.save(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            item.setItemStatus(0);
            item.setCreatedAt(LocalDateTime.now());
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                item.setSubtotal(item.getUnitPrice().multiply(item.getQuantity()).setScale(2, java.math.RoundingMode.HALF_UP));
            }
            itemMapper.insert(item);
        }
        return this.getById(order.getId());
    }

    public void update(Long id, Order order) {
        if (this.getById(id) == null) throw new BusinessException("订单不存在");
        order.setId(id);
        order.setOrderNo(null);
        order.setCreatedAt(null);
        this.updateById(order);
    }

    @Transactional
    public void updateWithItems(Long id, Order order, List<OrderItem> items) {
        if (this.getById(id) == null) throw new BusinessException("订单不存在");
        // 更新订单头
        order.setId(id);
        order.setOrderNo(null);
        order.setCreatedAt(null);
        order.setUpdatedAt(LocalDateTime.now());
        this.updateById(order);
        // 全量替换明细：删旧插新
        itemMapper.delete(new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, id));
        LocalDateTime now = LocalDateTime.now();
        for (OrderItem item : items) {
            item.setId(null);
            item.setOrderId(id);
            item.setItemStatus(0);
            item.setCreatedAt(now);
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                item.setSubtotal(item.getUnitPrice().multiply(item.getQuantity()).setScale(2, java.math.RoundingMode.HALF_UP));
            } else {
                item.setSubtotal(null);
            }
            itemMapper.insert(item);
        }
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("订单不存在");
        itemMapper.delete(new LambdaQueryWrapper<>(OrderItem.class).eq(OrderItem::getOrderId, id));
        this.removeById(id);
    }

    public Order detail(Long id) {
        Order order = this.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        return order;
    }

    public List<OrderItem> findItems(Long orderId) {
        return itemMapper.selectList(new LambdaQueryWrapper<>(OrderItem.class)
                .eq(OrderItem::getOrderId, orderId));
    }

    public PageResult<Order> page(int pageNum, int pageSize, String keyword,
                                   Long customerId, Integer status, LocalDate orderDate) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        if (customerId != null) {
            wrapper.eq(Order::getCustomerId, customerId);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (orderDate != null) {
            wrapper.eq(Order::getOrderDate, orderDate);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<Order> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    public void confirm(Long id) {
        Order order = this.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(1);
        this.updateById(order);
    }

    public void deliver(Long id) {
        Order order = this.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(2);
        this.updateById(order);
    }

    public void accept(Long id) {
        Order order = this.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(3);
        this.updateById(order);
    }

    public void settle(Long id) {
        Order order = this.getById(id);
        if (order == null) throw new BusinessException("订单不存在");
        order.setStatus(4);
        this.updateById(order);
    }

    public List<Order> recent(int days, Long customerId, String keyword) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (days > 0) {
            wrapper.ge(Order::getCreatedAt, LocalDateTime.now().minusDays(days));
        }
        if (customerId != null) {
            wrapper.eq(Order::getCustomerId, customerId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword).or().like(Order::getRemark, keyword));
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        return this.list(wrapper);
    }

    private int nextNo() {
        List<Order> today = this.list(new LambdaQueryWrapper<>(Order.class)
                .eq(Order::getOrderDate, LocalDate.now())
                .orderByDesc(Order::getId)
                .last("limit 1"));
        return today.isEmpty() ? 1 : (today.get(0).getId().intValue() % 9999 + 1);
    }
}