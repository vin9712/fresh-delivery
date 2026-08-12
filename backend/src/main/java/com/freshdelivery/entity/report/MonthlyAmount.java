package com.freshdelivery.entity.report;

import java.math.BigDecimal;

public record MonthlyAmount(String month, BigDecimal totalAmount) {
}