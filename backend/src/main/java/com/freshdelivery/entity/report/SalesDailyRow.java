package com.freshdelivery.entity.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesDailyRow(
        LocalDate deliveryDate,
        Long customerId,
        String customerName,
        Integer orderCount,
        BigDecimal totalAmount
) {
}