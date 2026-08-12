package com.freshdelivery.controller.purchase;

import com.freshdelivery.entity.purchase.PurchaseOrder;
import com.freshdelivery.entity.purchase.PurchaseItem;
import java.util.List;

public record CreatePurchaseOrderRequest(PurchaseOrder order, List<PurchaseItem> items) {}