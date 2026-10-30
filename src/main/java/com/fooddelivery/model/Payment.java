package com.fooddelivery.model;

public class Payment {
    private final String orderId;
    private final double amount;
    private final PaymentType type;
    private volatile PaymentStatus status;

    public Payment(String orderId, double amount, PaymentType type, PaymentStatus status) {
        this.orderId = orderId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public PaymentType getType() { return type; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}
