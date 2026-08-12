package com.freshdelivery.entity.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesDetailRow(
        LocalDate orderDate,
        Long customerId,
        String customerName,
        String itemName,
        String itemSpec,
        String itemUnit,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}