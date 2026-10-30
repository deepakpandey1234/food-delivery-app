package com.fooddelivery.payment;

import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;

public interface PaymentStrategy {
    PaymentType getType();
    PaymentStatus pay(String orderId, double amount);
}
