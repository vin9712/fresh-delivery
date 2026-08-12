package com.freshdelivery.controller.delivery;

import com.freshdelivery.entity.delivery.DeliveryOrder;
import com.freshdelivery.entity.delivery.DeliveryItem;
import java.util.List;

public record CreateDeliveryOrderRequest(DeliveryOrder order, List<DeliveryItem> items) {}