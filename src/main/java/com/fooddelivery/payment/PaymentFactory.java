package com.fooddelivery.payment;

import com.fooddelivery.model.PaymentType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the PaymentType sent by the app into a strategy object.
 * In Spring: inject List<PaymentStrategy> (all @Component beans) into this constructor.
 */
public class PaymentFactory {
    private final Map<PaymentType, PaymentStrategy> strategies = new EnumMap<>(PaymentType.class);

    public PaymentFactory(List<PaymentStrategy> all) {
        all.forEach(s -> strategies.put(s.getType(), s));
    }

    public PaymentStrategy get(PaymentType type) {
        PaymentStrategy s = strategies.get(type);
        if (s == null) throw new IllegalArgumentException("Unsupported payment type: " + type);
        return s;
    }
}
