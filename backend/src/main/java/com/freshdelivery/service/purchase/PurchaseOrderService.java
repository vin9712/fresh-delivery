package com.freshdelivery.service.purchase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.order.OrderItem;
import com.freshdelivery.entity.purchase.PurchaseItem;
import com.freshdelivery.entity.purchase.PurchaseOrder;
import com.freshdelivery.mapper.order.OrderItemMapper;
import com.freshdelivery.mapper.purchase.PurchaseItemMapper;
import com.freshdelivery.mapper.purchase.PurchaseOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> {

    @Autowired
    private PurchaseItemMapper purchaseItemMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String nextNo() {
        LocalDate today = LocalDate.now();
        String prefix = "PC" + today.format(FMT);
        List<PurchaseOrder> list = this.list(
                new LambdaQueryWrapper<PurchaseOrder>(PurchaseOrder.class)
                        .select(PurchaseOrder::getOrderNo)
                        .likeRight(PurchaseOrder::getOrderNo, prefix)
                        .orderByDesc(PurchaseOrder::getOrderNo)
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
    public PurchaseOrder create(PurchaseOrder order, List<PurchaseItem> items) {
        order.setOrderNo(this.nextNo());
        order.setStatus(0);
        order.setSourceType(order.getSourceType() == null ? 0 : order.getSourceType());
        order.setCreatedAt(LocalDateTime.now());
        this.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseItem item : items) {
            item.setOrderId(order.getId());
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                item.setSubtotal(item.getUnitPrice().multiply(item.getQuantity()));
            }
            item.setCreatedAt(LocalDateTime.now());
            totalAmount = totalAmount.add(item.getSubtotal() == null ? BigDecimal.ZERO : item.getSubtotal());
            purchaseItemMapper.insert(item);
        }
        order.setTotalAmount(totalAmount);
        this.updateById(order);
        return order;
    }

    @Transactional
    public void delete(Long id) {
        purchaseItemMapper.delete(new LambdaQueryWrapper<PurchaseItem>(PurchaseItem.class)
                .eq(PurchaseItem::getOrderId, id));
        this.removeById(id);
    }

    public PurchaseOrder detail(Long id) {
        return this.getById(id);
    }

    public List<PurchaseItem> findItems(Long orderId) {
        return purchaseItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseItem>(PurchaseItem.class)
                        .eq(PurchaseItem::getOrderId, orderId));
    }

    public Page<PurchaseOrder> page(int pageNum, int pageSize, String keyword,
                                    Long supplierId, Integer status, LocalDate startDate, LocalDate endDate) {
        Page<PurchaseOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>(PurchaseOrder.class);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(PurchaseOrder::getOrderNo, keyword);
        }
        if (supplierId != null) {
            wrapper.eq(PurchaseOrder::getSupplierId, supplierId);
        }
        if (status != null) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(PurchaseOrder::getOrderDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(PurchaseOrder::getOrderDate, endDate);
        }
        wrapper.orderByDesc(PurchaseOrder::getCreatedAt);
        return this.page(page, wrapper);
    }

    public void confirm(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order != null) {
            order.setStatus(1);
            this.updateById(order);
        }
    }

    public void stockIn(Long id) {
        PurchaseOrder order = this.getById(id);
        if (order != null) {
            order.setStatus(2);
            this.updateById(order);
        }
    }

    /**
     * 根据订单ID列表，汇总订单明细生成采购草稿。
     * 按 skuId+itemSpec 汇总数量，保留第一个 item 的品名/单位。
     */
    public List<PurchaseItem> aggregateFromOrders(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderItem> sourceItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>(OrderItem.class)
                        .in(OrderItem::getOrderId, orderIds));
        List<PurchaseItem> aggregated = new ArrayList<>();
        for (OrderItem src : sourceItems) {
            String key = (src.getSkuId() == null ? "0" : src.getSkuId().toString())
                    + "_" + (src.getItemSpec() == null ? "" : src.getItemSpec());
            boolean merged = false;
            for (PurchaseItem agg : aggregated) {
                String aggKey = (agg.getSkuId() == null ? "0" : agg.getSkuId().toString())
                        + "_" + (agg.getItemSpec() == null ? "" : agg.getItemSpec());
                if (key.equals(aggKey)) {
                    agg.setQuantity(agg.getQuantity().add(src.getQuantity()));
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                PurchaseItem item = new PurchaseItem();
                item.setSkuId(src.getSkuId());
                item.setItemName(src.getItemName());
                item.setItemSpec(src.getItemSpec());
                item.setItemUnit(src.getItemUnit());
                item.setQuantity(src.getQuantity());
                item.setUnitPrice(src.getUnitPrice());
                aggregated.add(item);
            }
        }
        return aggregated;
    }
}