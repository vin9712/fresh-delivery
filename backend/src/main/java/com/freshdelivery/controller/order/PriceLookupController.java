package com.freshdelivery.controller.order;

import com.freshdelivery.common.Result;
import com.freshdelivery.service.order.PriceLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/order/price")
@RequiredArgsConstructor
public class PriceLookupController {

    private final PriceLookupService priceLookupService;

    @GetMapping("/lookup")
    public Result<BigDecimal> getPrice(@RequestParam Long customerId,
                                        @RequestParam Long skuId,
                                        @RequestParam(defaultValue = "today") LocalDate orderDate) {
        BigDecimal price = priceLookupService.getPrice(customerId, skuId, orderDate);
        return Result.ok(price);
    }
}