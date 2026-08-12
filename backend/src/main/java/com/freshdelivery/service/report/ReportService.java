package com.freshdelivery.service.report;

import com.freshdelivery.entity.report.*;
import com.freshdelivery.mapper.report.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    public List<SalesDailyRow> salesDaily(LocalDate start, LocalDate end) {
        return reportMapper.selectSalesDaily(start, end);
    }

    public List<MonthlyRow> monthly(LocalDate start, LocalDate end) {
        return reportMapper.selectMonthly(start, end);
    }

    public List<ProfitRow> profit(LocalDate start, LocalDate end) {
        List<MonthlyAmount> sales = reportMapper.selectSalesByMonth(start, end);
        List<MonthlyAmount> purchases = reportMapper.selectPurchaseByMonth(start, end);

        Map<String, BigDecimal> salesMap = sales.stream()
                .collect(Collectors.toMap(MonthlyAmount::month, MonthlyAmount::totalAmount));
        Map<String, BigDecimal> purchaseMap = purchases.stream()
                .collect(Collectors.toMap(MonthlyAmount::month, MonthlyAmount::totalAmount));

        Set<String> months = new java.util.TreeSet<>();
        months.addAll(salesMap.keySet());
        months.addAll(purchaseMap.keySet());

        return months.stream()
                .map(m -> {
                    BigDecimal s = salesMap.getOrDefault(m, BigDecimal.ZERO);
                    BigDecimal p = purchaseMap.getOrDefault(m, BigDecimal.ZERO);
                    return new ProfitRow(m, s, p, s.subtract(p));
                })
                .collect(Collectors.toList());
    }

    public List<SalesDetailRow> salesDetail(LocalDate start, LocalDate end, Long customerId) {
        return reportMapper.selectSalesDetail(start, end, customerId);
    }

    public List<LossRow> lossReport(LocalDate start, LocalDate end, Long customerId) {
        return reportMapper.selectLossReport(start, end, customerId);
    }

    public List<PurchaseRow> purchaseReport(LocalDate start, LocalDate end, Long supplierId) {
        return reportMapper.selectPurchaseReport(start, end, supplierId);
    }

    public List<StatementRow> statement(LocalDate start, LocalDate end, Long customerId) {
        return reportMapper.selectStatement(start, end, customerId);
    }
}