package com.fooddelivery.delivery;

import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Location;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** In-memory. In production: Redis GEO / PostGIS radius query. */
public class PartnerRepository {
    private final List<DeliveryPartner> partners = new CopyOnWriteArrayList<>();

    public void add(DeliveryPartner p) { partners.add(p); }

    public List<DeliveryPartner> findNearby(Location point, double radiusKm) {
        return partners.stream().filter(p -> p.getLocation().distanceTo(point) <= radiusKm).toList();
    }
}
