package com.fooddelivery.observer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;

public class UserNotifier implements OrderObserver {
    private final NotificationService notifications;
    public UserNotifier(NotificationService n) { this.notifications = n; }

    @Override
    public void onStatusChange(Order order, OrderStatus status) {
        notifications.send("USER:" + order.getUser().getId(), "Order " + order.getId() + " is " + status);
    }
}
