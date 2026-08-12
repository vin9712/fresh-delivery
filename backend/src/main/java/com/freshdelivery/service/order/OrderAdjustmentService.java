package com.freshdelivery.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.exception.BusinessException;
import com.freshdelivery.entity.order.OrderAdjustment;
import com.freshdelivery.entity.order.OrderItem;
import com.freshdelivery.mapper.order.OrderAdjustmentMapper;
import com.freshdelivery.mapper.order.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderAdjustmentService extends ServiceImpl<OrderAdjustmentMapper, OrderAdjustment> {

    @Autowired private OrderItemMapper itemMapper;

    public OrderAdjustment create(OrderAdjustment adjustment) {
        if (adjustment.getOriginItemId() != null) {
            OrderItem originItem = itemMapper.selectById(adjustment.getOriginItemId());
            if (originItem != null && (adjustment.getItemName() == null || adjustment.getItemName().isBlank())) {
                adjustment.setItemName(originItem.getItemName());
                adjustment.setItemSpec(originItem.getItemSpec());
                adjustment.setItemUnit(originItem.getItemUnit());
                adjustment.setSkuId(originItem.getSkuId());
            }
        }
        adjustment.setAdjustDate(LocalDate.now());
        adjustment.setCreatedAt(LocalDateTime.now());
        this.save(adjustment);

        if (adjustment.getOriginItemId() != null && adjustment.getAdjustType() == 1) {
            OrderItem item = itemMapper.selectById(adjustment.getOriginItemId());
            if (item != null) {
                item.setItemStatus(1);
                itemMapper.updateById(item);
            }
        }
        return this.getById(adjustment.getId());
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("调整记录不存在");
        this.removeById(id);
    }

    public PageResult<OrderAdjustment> page(int pageNum, int pageSize,
                                             Long orderId, Integer adjustType, LocalDate orderDate) {
        LambdaQueryWrapper<OrderAdjustment> wrapper = new LambdaQueryWrapper<>();
        if (orderId != null) {
            wrapper.eq(OrderAdjustment::getOrderId, orderId);
        }
        if (adjustType != null) {
            wrapper.eq(OrderAdjustment::getAdjustType, adjustType);
        }
        if (orderDate != null) {
            wrapper.eq(OrderAdjustment::getOrderDate, orderDate);
        }
        wrapper.orderByDesc(OrderAdjustment::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<OrderAdjustment> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}