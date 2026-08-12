package com.freshdelivery.entity.report;

import java.math.BigDecimal;

public record ProfitRow(
        String month,
        BigDecimal salesTotal,
        BigDecimal purchaseTotal,
        BigDecimal profit
) {
}