package com.fooddelivery.observer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public interface OrderObserver {
    void onStatusChange(Order order, OrderStatus newStatus);
}
