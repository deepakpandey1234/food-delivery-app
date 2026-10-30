package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class CancelledState extends AbstractOrderState {
    public static final CancelledState INSTANCE = new CancelledState();
    private CancelledState() {}

    // terminal state: every action is rejected

    @Override public OrderStatus getStatus() { return OrderStatus.CANCELLED; }
}
