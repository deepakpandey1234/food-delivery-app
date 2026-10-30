package com.fooddelivery.delivery;

import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;

/** Fairness: give work to the partner who has done the fewest deliveries today. */
public class LeastBusyStrategy implements AssignmentStrategy {
    public List<DeliveryPartner> rank(Order order, List<DeliveryPartner> candidates) {
        return candidates.stream()
                .filter(DeliveryPartner::isAvailable)
                .sorted(Comparator.comparingInt(DeliveryPartner::getCompletedToday))
                .toList();
    }
}
