package com.fooddelivery.payment;

/** External UPI / card provider. orderId doubles as the idempotency key. */
@FunctionalInterface
public interface PaymentGateway {
    boolean charge(String idempotencyKey, double amount);
}
