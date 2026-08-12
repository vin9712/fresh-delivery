package com.freshdelivery.controller.delivery;

import com.freshdelivery.entity.delivery.Acceptance;
import com.freshdelivery.entity.delivery.AcceptanceItem;
import java.util.List;

public record CreateAcceptanceRequest(Acceptance acceptance, List<AcceptanceItem> items) {}