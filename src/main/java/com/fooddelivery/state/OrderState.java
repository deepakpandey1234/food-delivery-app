package com.fooddelivery.state;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Named actions instead of a generic next(): a pickup event can never confirm an order. */
public interface OrderState {
    void confirm(Order order);
    void startPreparing(Order order);
    void pickUp(Order order);
    void deliver(Order order);
    void cancel(Order order);
    OrderStatus getStatus();
}
