package com.freshdelivery.controller.report;

import com.freshdelivery.entity.report.*;
import com.freshdelivery.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<List<SalesDailyRow>> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.salesDaily(startDate, endDate));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyRow>> monthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.monthly(startDate, endDate));
    }

    @GetMapping("/profit")
    public ResponseEntity<List<ProfitRow>> profit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.profit(startDate, endDate));
    }

    @GetMapping("/detail")
    public ResponseEntity<List<SalesDetailRow>> detail(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(reportService.salesDetail(startDate, endDate, customerId));
    }

    @GetMapping("/loss")
    public ResponseEntity<List<LossRow>> loss(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(reportService.lossReport(startDate, endDate, customerId));
    }

    @GetMapping("/purchase")
    public ResponseEntity<List<PurchaseRow>> purchase(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long supplierId) {
        return ResponseEntity.ok(reportService.purchaseReport(startDate, endDate, supplierId));
    }

    @GetMapping("/statement")
    public ResponseEntity<List<StatementRow>> statement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId) {
        return ResponseEntity.ok(reportService.statement(startDate, endDate, customerId));
    }
}