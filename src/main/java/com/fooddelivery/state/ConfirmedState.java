package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class ConfirmedState extends AbstractOrderState {
    public static final ConfirmedState INSTANCE = new ConfirmedState();
    private ConfirmedState() {}

    @Override public void startPreparing(Order o) { o.transitionTo(PreparingState.INSTANCE); }
    @Override public void cancel(Order o)         { o.transitionTo(CancelledState.INSTANCE); } // refund handled by observer

    @Override public OrderStatus getStatus() { return OrderStatus.CONFIRMED; }
}
