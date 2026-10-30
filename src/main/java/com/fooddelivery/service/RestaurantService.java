package com.fooddelivery.service;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.FoodPreference;
import com.fooddelivery.model.Location;
import com.fooddelivery.model.Restaurant;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantService {
    private final Map<String, Restaurant> restaurants = new ConcurrentHashMap<>();

    public void register(Restaurant r) { restaurants.put(r.getId(), r); }
    public Optional<Restaurant> find(String id) { return Optional.ofNullable(restaurants.get(id)); }

    /** Open restaurants within radius, nearest first. */
    public List<Restaurant> listNearby(Location userLocation, double radiusKm) {
        return restaurants.values().stream()
                .filter(Restaurant::isOpen)
                .filter(r -> r.getLocation().distanceTo(userLocation) <= radiusKm)
                .sorted(Comparator.comparingDouble(r -> r.getLocation().distanceTo(userLocation)))
                .toList();
    }

    /** Menu filtered by the user's veg / non-veg preference. */
    public List<FoodItem> menuFor(String restaurantId, FoodPreference pref) {
        return find(restaurantId).map(r -> r.getMenu().stream()
                        .filter(FoodItem::isAvailable)
                        .filter(i -> pref.allows(i.getType()))
                        .toList())
                .orElse(List.of());
    }
}
