package com.freshdelivery.entity.report;

import java.math.BigDecimal;

public record StatementRow(
        String month,
        Long customerId,
        String customerName,
        Integer orderCount,
        BigDecimal totalAmount,
        BigDecimal lossAmount,
        Integer settlementCycle
) {
}