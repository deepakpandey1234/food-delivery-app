package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class OutForDeliveryState extends AbstractOrderState {
    public static final OutForDeliveryState INSTANCE = new OutForDeliveryState();
    private OutForDeliveryState() {}

    @Override public void deliver(Order o) { o.transitionTo(DeliveredState.INSTANCE); }

    @Override public OrderStatus getStatus() { return OrderStatus.OUT_FOR_DELIVERY; }
}
