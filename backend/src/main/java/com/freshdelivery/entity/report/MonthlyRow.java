package com.freshdelivery.entity.report;

import java.math.BigDecimal;

public record MonthlyRow(
        String month,
        Long customerId,
        String customerName,
        Integer orderCount,
        BigDecimal totalAmount,
        BigDecimal lossAmount
) {
}