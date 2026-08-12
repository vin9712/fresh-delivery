package com.freshdelivery.controller.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.entity.sys.SysOperationLog;
import com.freshdelivery.service.sys.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/log")
@RequiredArgsConstructor
public class SysOperationLogController {

    private final SysOperationLogService logService;

    @GetMapping("/page")
    public Result<PageResult<SysOperationLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysOperationLog::getUserName, keyword)
                    .or().like(SysOperationLog::getModule, keyword));
        }
        wrapper.orderByDesc(SysOperationLog::getOperateTime);

        Page<SysOperationLog> p = logService.page(
                new Page<>(pageNum, pageSize), wrapper);

        PageResult<SysOperationLog> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return Result.ok(result);
    }
}