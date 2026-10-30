package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class PreparingState extends AbstractOrderState {
    public static final PreparingState INSTANCE = new PreparingState();
    private PreparingState() {}

    @Override public void pickUp(Order o) { o.transitionTo(OutForDeliveryState.INSTANCE); }

    @Override public OrderStatus getStatus() { return OrderStatus.PREPARING; }
}
