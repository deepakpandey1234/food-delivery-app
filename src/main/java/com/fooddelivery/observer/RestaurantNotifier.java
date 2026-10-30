package com.fooddelivery.observer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

/** Restaurant only cares about new confirmed orders and cancellations. */
public class RestaurantNotifier implements OrderObserver {
    private final NotificationService notifications;
    public RestaurantNotifier(NotificationService n) { this.notifications = n; }

    @Override
    public void onStatusChange(Order order, OrderStatus status) {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.CANCELLED) {
            notifications.send("RESTAURANT:" + order.getRestaurant().getId(),
                    "Order " + order.getId() + " is " + status);
        }
    }
}
