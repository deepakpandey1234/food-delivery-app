package com.fooddelivery.payment;

import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;

/** No gateway call: money is collected at the door, so status is PENDING. */
public class CashOnDeliveryPayment implements PaymentStrategy {
    public PaymentType getType() { return PaymentType.COD; }
    public PaymentStatus pay(String orderId, double amount) { return PaymentStatus.PENDING; }
}
