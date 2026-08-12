package com.freshdelivery.entity.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseRow(
        LocalDate orderDate,
        Long supplierId,
        String supplierName,
        String itemName,
        String itemSpec,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}