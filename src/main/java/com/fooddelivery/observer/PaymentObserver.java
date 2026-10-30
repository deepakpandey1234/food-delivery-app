package com.fooddelivery.observer;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.payment.PaymentService;

/** Refund on cancel; mark COD cash as collected on delivery. */
public class PaymentObserver implements OrderObserver {
    private final PaymentService paymentService;
    public PaymentObserver(PaymentService ps) { this.paymentService = ps; }

    @Override
    public void onStatusChange(Order order, OrderStatus status) {
        if (status == OrderStatus.CANCELLED) paymentService.refundIfPaid(order.getId());
        if (status == OrderStatus.DELIVERED) paymentService.markCollected(order.getId());
    }
}
