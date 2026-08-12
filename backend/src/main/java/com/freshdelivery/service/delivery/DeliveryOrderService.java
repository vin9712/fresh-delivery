package com.freshdelivery.service.delivery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.delivery.DeliveryOrder;
import com.freshdelivery.entity.delivery.DeliveryItem;
import com.freshdelivery.mapper.delivery.DeliveryOrderMapper;
import com.freshdelivery.mapper.delivery.DeliveryItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DeliveryOrderService extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> {

    @Autowired
    private DeliveryItemMapper deliveryItemMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String nextNo() {
        LocalDate today = LocalDate.now();
        String prefix = "HS" + today.format(FMT);
        List<DeliveryOrder> list = this.list(
                new LambdaQueryWrapper<DeliveryOrder>(DeliveryOrder.class)
                        .select(DeliveryOrder::getOrderNo)
                        .likeRight(DeliveryOrder::getOrderNo, prefix)
                        .orderByDesc(DeliveryOrder::getOrderNo)
                        .last("LIMIT 1")
        );
        int next = 1;
        if (!list.isEmpty()) {
            String lastNo = list.get(0).getOrderNo();
            String numPart = lastNo.substring(prefix.length());
            if (!numPart.isEmpty()) {
                next = Integer.parseInt(numPart) + 1;
            }
        }
        return prefix + String.format("%03d", next);
    }

    @Transactional
    public DeliveryOrder create(DeliveryOrder order, List<DeliveryItem> items) {
        order.setOrderNo(this.nextNo());
        order.setStatus(0);
        order.setPrintCount(0);
        order.setCreatedAt(LocalDateTime.now());
        this.save(order);

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (DeliveryItem item : items) {
            item.setDeliveryOrderId(order.getId());
            item.setAdjustStatus(item.getAdjustStatus() == null ? 0 : item.getAdjustStatus());
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                item.setSubtotal(item.getUnitPrice().multiply(item.getQuantity()));
            }
            totalQuantity = totalQuantity.add(item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity());
            totalAmount = totalAmount.add(item.getSubtotal() == null ? BigDecimal.ZERO : item.getSubtotal());
            deliveryItemMapper.insert(item);
        }
        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        this.updateById(order);
        return order;
    }

    @Transactional
    public void delete(Long id) {
        deliveryItemMapper.delete(new LambdaQueryWrapper<DeliveryItem>(DeliveryItem.class)
                .eq(DeliveryItem::getDeliveryOrderId, id));
        this.removeById(id);
    }

    public DeliveryOrder detail(Long id) {
        return this.getById(id);
    }

    public List<DeliveryItem> findItems(Long deliveryOrderId) {
        return deliveryItemMapper.selectList(
                new LambdaQueryWrapper<DeliveryItem>(DeliveryItem.class)
                        .eq(DeliveryItem::getDeliveryOrderId, deliveryOrderId));
    }

    public Page<DeliveryOrder> page(int pageNum, int pageSize, String keyword,
                                    Long customerId, Integer status, LocalDate startDate, LocalDate endDate) {
        Page<DeliveryOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>(DeliveryOrder.class);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(DeliveryOrder::getOrderNo, keyword);
        }
        if (customerId != null) {
            wrapper.eq(DeliveryOrder::getCustomerId, customerId);
        }
        if (status != null) {
            wrapper.eq(DeliveryOrder::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(DeliveryOrder::getDeliveryDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(DeliveryOrder::getDeliveryDate, endDate);
        }
        wrapper.orderByDesc(DeliveryOrder::getCreatedAt);
        return this.page(page, wrapper);
    }

    public void markDelivered(Long id) {
        DeliveryOrder order = this.getById(id);
        if (order != null) {
            order.setStatus(1);
            this.updateById(order);
        }
    }

    public void markAccepted(Long id) {
        DeliveryOrder order = this.getById(id);
        if (order != null) {
            order.setStatus(2);
            this.updateById(order);
        }
    }

    public void printDelivery(Long id) {
        DeliveryOrder order = this.getById(id);
        if (order != null) {
            order.setPrintCount(order.getPrintCount() + 1);
            this.updateById(order);
        }
    }
}