package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Stateless, so a single shared instance is safe. */
public final class PlacedState extends AbstractOrderState {
    public static final PlacedState INSTANCE = new PlacedState();
    private PlacedState() {}

    @Override public void confirm(Order o) { o.transitionTo(ConfirmedState.INSTANCE); }
    @Override public void cancel(Order o)  { o.transitionTo(CancelledState.INSTANCE); }

    @Override public OrderStatus getStatus() { return OrderStatus.PLACED; }
}
