package com.fooddelivery.observer;

import com.fooddelivery.delivery.DeliveryService;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Assign a partner while food is being cooked; free the partner when the order ends. */
public class DeliveryAssignmentObserver implements OrderObserver {
    private final DeliveryService deliveryService;
    public DeliveryAssignmentObserver(DeliveryService ds) { this.deliveryService = ds; }

    @Override
    public void onStatusChange(Order order, OrderStatus status) {
        switch (status) {
            case PREPARING -> deliveryService.assign(order);
            case DELIVERED -> deliveryService.release(order, true);
            case CANCELLED -> deliveryService.release(order, false);
            default -> { }
        }
    }
}
