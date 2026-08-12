package com.freshdelivery.controller.sys;

import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.Result;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.sys.SysApproval;
import com.freshdelivery.service.sys.SysApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/approval")
@RequiredArgsConstructor
public class SysApprovalController {

    private final SysApprovalService approvalService;

    @GetMapping("/page")
    public Result<PageResult<SysApproval>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bizType) {
        return Result.ok(approvalService.page(pageNum, pageSize, bizType));
    }

    @GetMapping("/pending")
    public Result<PageResult<SysApproval>> pending(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(approvalService.getPending(pageNum, pageSize));
    }

    @PostMapping("/submit")
    @OperationLog(module = "approval", action = "submit")
    public Result<Void> submit(@RequestBody SysApproval approval) {
        approvalService.submit(approval);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "approval", action = "handle")
    public Result<Void> handle(@PathVariable Long id, @RequestBody HandleForm form) {
        approvalService.handle(id, form.status(), form.remark());
        return Result.ok();
    }

    public record HandleForm(Integer status, String remark) {}
}