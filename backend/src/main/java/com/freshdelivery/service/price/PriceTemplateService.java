package com.freshdelivery.service.price;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.entity.price.PriceTemplate;
import com.freshdelivery.entity.price.PriceTemplateSku;
import com.freshdelivery.mapper.price.PriceTemplateMapper;
import com.freshdelivery.mapper.price.PriceTemplateSkuMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceTemplateService extends ServiceImpl<PriceTemplateMapper, PriceTemplate> {

    @Autowired private PriceTemplateSkuMapper templateSkuMapper;

    @Transactional
    public PriceTemplate create(PriceTemplate template) {
        template.setStatus(template.getStatus() == null ? 1 : template.getStatus());
        template.setCreatedAt(LocalDateTime.now());
        this.save(template);
        return template;
    }

    public void update(Long id, PriceTemplate template) {
        if (this.getById(id) == null) throw new BusinessException("报价方案不存在");
        template.setId(id);
        this.updateById(template);
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("报价方案不存在");
        templateSkuMapper.delete(new LambdaQueryWrapper<>(PriceTemplateSku.class)
                .eq(PriceTemplateSku::getTemplateId, id));
        this.removeById(id);
    }

    public PriceTemplate detail(Long id) {
        PriceTemplate template = this.getById(id);
        if (template == null) throw new BusinessException("报价方案不存在");
        return template;
    }

    public List<PriceTemplateSku> findSkuList(Long templateId) {
        return templateSkuMapper.selectList(new LambdaQueryWrapper<>(PriceTemplateSku.class)
                .eq(PriceTemplateSku::getTemplateId, templateId));
    }

    @Transactional
    public void saveSkuList(Long templateId, List<PriceTemplateSku> skuList) {
        templateSkuMapper.delete(new LambdaQueryWrapper<>(PriceTemplateSku.class)
                .eq(PriceTemplateSku::getTemplateId, templateId));
        for (PriceTemplateSku sku : skuList) {
            sku.setTemplateId(templateId);
            templateSkuMapper.insert(sku);
        }
    }

    public PageResult<PriceTemplate> page(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<PriceTemplate> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(PriceTemplate::getName, keyword);
        }
        wrapper.orderByDesc(PriceTemplate::getCreatedAt);

        var p = this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<PriceTemplate> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}