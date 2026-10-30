package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class DeliveredState extends AbstractOrderState {
    public static final DeliveredState INSTANCE = new DeliveredState();
    private DeliveredState() {}

    // terminal state: every action is rejected

    @Override public OrderStatus getStatus() { return OrderStatus.DELIVERED; }
}
