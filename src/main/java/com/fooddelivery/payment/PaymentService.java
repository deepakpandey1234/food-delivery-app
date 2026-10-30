package com.fooddelivery.payment;

import com.fooddelivery.model.Payment;
import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    /**
     * Idempotent: if this order already has a SUCCESS / PENDING payment, return it
     * instead of charging again (protects against double-click and client retries).
     */
    public PaymentStatus processPayment(String orderId, double amount, PaymentStrategy strategy) {
        Payment existing = payments.get(orderId);
        if (existing != null && existing.getStatus() != PaymentStatus.FAILED) {
            return existing.getStatus();
        }
        PaymentStatus status = strategy.pay(orderId, amount);
        payments.put(orderId, new Payment(orderId, amount, strategy.getType(), status));
        return status;
    }

    public void refundIfPaid(String orderId) {
        Payment p = payments.get(orderId);
        if (p == null) return;
        if (p.getStatus() == PaymentStatus.SUCCESS) {
            p.setStatus(PaymentStatus.REFUNDED);           // call gateway refund API here
        } else if (p.getStatus() == PaymentStatus.PENDING) {
            p.setStatus(PaymentStatus.FAILED);             // COD cancelled: nothing to collect
        }
    }

    public void markCollected(String orderId) {
        Payment p = payments.get(orderId);
        if (p != null && p.getType() == PaymentType.COD && p.getStatus() == PaymentStatus.PENDING) {
            p.setStatus(PaymentStatus.SUCCESS);
        }
    }

    public Optional<Payment> find(String orderId) { return Optional.ofNullable(payments.get(orderId)); }
}
