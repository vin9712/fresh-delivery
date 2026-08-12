package com.freshdelivery.controller.delivery;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.entity.delivery.Acceptance;
import com.freshdelivery.entity.delivery.AcceptanceItem;
import com.freshdelivery.service.delivery.AcceptanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/delivery/acceptance")
public class AcceptanceController {

    @Autowired
    private AcceptanceService acceptanceService;

    @GetMapping("/page")
    public ResponseEntity<Page<Acceptance>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(acceptanceService.page(pageNum, pageSize, keyword, customerId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Acceptance> create(@RequestBody CreateAcceptanceRequest req) {
        return ResponseEntity.ok(acceptanceService.create(req.acceptance(), req.items()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Acceptance> detail(@PathVariable Long id) {
        return ResponseEntity.ok(acceptanceService.detail(id));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<AcceptanceItem>> items(@PathVariable Long id) {
        return ResponseEntity.ok(acceptanceService.findItems(id));
    }
}