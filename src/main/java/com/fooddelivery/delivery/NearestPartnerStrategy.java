package com.fooddelivery.delivery;

import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Location;
import com.fooddelivery.model.Order;

import java.util.Comparator;
import java.util.List;

public class NearestPartnerStrategy implements AssignmentStrategy {
    public List<DeliveryPartner> rank(Order order, List<DeliveryPartner> candidates) {
        Location r = order.getRestaurant().getLocation();
        return candidates.stream()
                .filter(DeliveryPartner::isAvailable)
                .sorted(Comparator.comparingDouble(p -> p.getLocation().distanceTo(r)))
                .toList();
    }
}
