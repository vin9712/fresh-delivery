package com.freshdelivery.entity.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LossRow(
        LocalDate deliveryDate,
        Long customerId,
        String customerName,
        String itemName,
        String itemSpec,
        BigDecimal deliveredQuantity,
        BigDecimal actualQuantity,
        BigDecimal lossQuantity,
        BigDecimal unitPrice
) {
}