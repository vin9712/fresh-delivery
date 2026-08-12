package com.freshdelivery.service.delivery;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.entity.delivery.Acceptance;
import com.freshdelivery.entity.delivery.AcceptanceItem;
import com.freshdelivery.entity.delivery.DeliveryOrder;
import com.freshdelivery.mapper.delivery.AcceptanceMapper;
import com.freshdelivery.mapper.delivery.AcceptanceItemMapper;
import com.freshdelivery.mapper.delivery.DeliveryOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AcceptanceService extends ServiceImpl<AcceptanceMapper, Acceptance> {

    @Autowired
    private AcceptanceItemMapper acceptanceItemMapper;

    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String nextNo() {
        LocalDate today = LocalDate.now();
        String prefix = "YS" + today.format(FMT);
        List<Acceptance> list = this.list(
                new LambdaQueryWrapper<Acceptance>(Acceptance.class)
                        .select(Acceptance::getOrderNo)
                        .likeRight(Acceptance::getOrderNo, prefix)
                        .orderByDesc(Acceptance::getOrderNo)
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
    public Acceptance create(Acceptance acceptance, List<AcceptanceItem> items) {
        // 1:1 — ensure no existing acceptance for this delivery order
        Acceptance existing = this.getOne(
                new LambdaQueryWrapper<Acceptance>(Acceptance.class)
                        .eq(Acceptance::getDeliveryOrderId, acceptance.getDeliveryOrderId()));
        if (existing != null) {
            throw new RuntimeException("该送货单已存在验收记录");
        }

        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectById(acceptance.getDeliveryOrderId());
        if (deliveryOrder == null) {
            throw new RuntimeException("送货单不存在");
        }

        acceptance.setOrderNo(this.nextNo());
        acceptance.setDeliveryDate(deliveryOrder.getDeliveryDate());
        acceptance.setDeliveryOrderNo(deliveryOrder.getOrderNo());
        acceptance.setCustomerId(deliveryOrder.getCustomerId());
        acceptance.setPointId(deliveryOrder.getPointId());
        acceptance.setCreatedAt(LocalDateTime.now());
        this.save(acceptance);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalLossAmount = BigDecimal.ZERO;
        for (AcceptanceItem item : items) {
            item.setAcceptanceId(acceptance.getId());
            BigDecimal delivered = item.getDeliveredQuantity() == null ? BigDecimal.ZERO : item.getDeliveredQuantity();
            BigDecimal actual = item.getActualQuantity() == null ? BigDecimal.ZERO : item.getActualQuantity();
            BigDecimal loss = actual.subtract(delivered);
            item.setLossQuantity(loss);
            if (item.getUnitPrice() != null) {
                item.setActualAmount(actual.multiply(item.getUnitPrice()));
            }
            totalAmount = totalAmount.add(item.getActualAmount() == null ? BigDecimal.ZERO : item.getActualAmount());
            if (loss.signum() < 0) {
                totalLossAmount = totalLossAmount.add(loss.abs());
            }
            acceptanceItemMapper.insert(item);
        }
        acceptance.setTotalAmount(totalAmount);
        acceptance.setTotalLossAmount(totalLossAmount);
        this.updateById(acceptance);

        // Auto-set delivery order status to 2 (已验收)
        deliveryOrder.setStatus(2);
        deliveryOrderMapper.updateById(deliveryOrder);
        return acceptance;
    }

    public Acceptance detail(Long id) {
        return this.getById(id);
    }

    public List<AcceptanceItem> findItems(Long acceptanceId) {
        return acceptanceItemMapper.selectList(
                new LambdaQueryWrapper<AcceptanceItem>(AcceptanceItem.class)
                        .eq(AcceptanceItem::getAcceptanceId, acceptanceId));
    }

    public Page<Acceptance> page(int pageNum, int pageSize, String keyword,
                                 Long customerId, LocalDate startDate, LocalDate endDate) {
        Page<Acceptance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Acceptance> wrapper = new LambdaQueryWrapper<>(Acceptance.class);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Acceptance::getOrderNo, keyword);
        }
        if (customerId != null) {
            wrapper.eq(Acceptance::getCustomerId, customerId);
        }
        if (startDate != null) {
            wrapper.ge(Acceptance::getDeliveryDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Acceptance::getDeliveryDate, endDate);
        }
        wrapper.orderByDesc(Acceptance::getCreatedAt);
        return this.page(page, wrapper);
    }
}