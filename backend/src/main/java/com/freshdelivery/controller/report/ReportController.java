package com.freshdelivery.controller.report;

import com.freshdelivery.common.Result;
import com.freshdelivery.entity.report.*;
import com.freshdelivery.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    public Result<List<SalesDailyRow>> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.salesDaily(startDate, endDate));
    }

    @GetMapping("/monthly")
    public Result<List<MonthlyRow>> monthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.monthly(startDate, endDate));
    }

    @GetMapping("/profit")
    public Result<List<ProfitRow>> profit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.ok(reportService.profit(startDate, endDate));
    }

    @GetMapping("/detail")
    public Result<List<SalesDetailRow>> detail(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return Result.ok(reportService.salesDetail(startDate, endDate, customerId));
    }

    @GetMapping("/loss")
    public Result<List<LossRow>> loss(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return Result.ok(reportService.lossReport(startDate, endDate, customerId));
    }

    @GetMapping("/purchase")
    public Result<List<PurchaseRow>> purchase(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long supplierId) {
        return Result.ok(reportService.purchaseReport(startDate, endDate, supplierId));
    }

    @GetMapping("/statement")
    public Result<List<StatementRow>> statement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return Result.ok(reportService.statement(startDate, endDate, customerId));
    }
}
