package com.fooddelivery.model;

/** Kept separate from OrderStatus on purpose: a COD order is CONFIRMED while payment is PENDING. */
public enum PaymentStatus { PENDING, SUCCESS, FAILED, REFUNDED }
