package com.fooddelivery.state;

import com.fooddelivery.exception.InvalidOrderStateException;
import com.fooddelivery.model.Order;

/** Default = reject. Each concrete state overrides only the moves it allows. */
public abstract class AbstractOrderState implements OrderState {
    protected InvalidOrderStateException invalid(String action) {
        return new InvalidOrderStateException("Cannot " + action + " when order is " + getStatus());
    }
    public void confirm(Order o)        { throw invalid("confirm"); }
    public void startPreparing(Order o) { throw invalid("start preparing"); }
    public void pickUp(Order o)         { throw invalid("pick up"); }
    public void deliver(Order o)        { throw invalid("deliver"); }
    public void cancel(Order o)         { throw invalid("cancel"); }
}
