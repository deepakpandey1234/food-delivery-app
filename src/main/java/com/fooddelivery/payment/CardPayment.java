package com.fooddelivery.payment;

import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;

public class CardPayment implements PaymentStrategy {
    private final PaymentGateway gateway;
    public CardPayment(PaymentGateway gateway) { this.gateway = gateway; }

    public PaymentType getType() { return PaymentType.CARD; }
    public PaymentStatus pay(String orderId, double amount) {
        return gateway.charge(orderId, amount) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
