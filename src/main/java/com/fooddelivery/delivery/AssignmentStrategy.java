package com.fooddelivery.delivery;

import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Order;

import java.util.List;

/** Returns candidates in PREFERENCE order; DeliveryService claims the first one it can. */
public interface AssignmentStrategy {
    List<DeliveryPartner> rank(Order order, List<DeliveryPartner> candidates);
}
