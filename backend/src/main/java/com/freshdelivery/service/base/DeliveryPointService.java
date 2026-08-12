package com.freshdelivery.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.base.DeliveryPoint;
import com.freshdelivery.mapper.base.DeliveryPointMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryPointService extends ServiceImpl<DeliveryPointMapper, DeliveryPoint> {

    public DeliveryPoint create(DeliveryPoint point) {
        point.setStatus(point.getStatus() == null ? 1 : point.getStatus());
        point.setCreatedAt(LocalDateTime.now());
        this.save(point);
        return point;
    }

    public void update(Long id, DeliveryPoint point) {
        if (this.getById(id) == null) throw new BusinessException("配送点不存在");
        point.setId(id);
        this.updateById(point);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("配送点不存在");
        this.removeById(id);
    }

    public List<DeliveryPoint> findByCustomerId(Long customerId) {
        return this.list(new LambdaQueryWrapper<>(DeliveryPoint.class)
                .eq(DeliveryPoint::getCustomerId, customerId));
    }

    public PageResult<DeliveryPoint> page(int pageNum, int pageSize, String keyword, Long customerId) {
        LambdaQueryWrapper<DeliveryPoint> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(DeliveryPoint::getName, keyword)
                    .or().like(DeliveryPoint::getContactPerson, keyword)
                    .or().like(DeliveryPoint::getPhone, keyword));
        }
        if (customerId != null) {
            wrapper.eq(DeliveryPoint::getCustomerId, customerId);
        }
        wrapper.orderByDesc(DeliveryPoint::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<DeliveryPoint> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}