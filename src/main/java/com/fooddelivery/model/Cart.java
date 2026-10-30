package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** One cart per user. Items may only come from ONE restaurant at a time. */
public class Cart {
    private final String userId;
    private Restaurant restaurant;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String userId) { this.userId = userId; }


    public void put(CartItem item, Restaurant from) {
        this.restaurant = from;
        items.merge(item.itemId(), item, (old, add) -> old.withQuantity(old.quantity() + add.quantity()));
    }

    public void remove(String itemId) {
        items.remove(itemId);
        if (items.isEmpty()) restaurant = null;
    }

    public void clear() {
        items.clear();
        restaurant = null;
    }

    public double total() { return items.values().stream().mapToDouble(CartItem::total).sum(); }
    public boolean isEmpty() { return items.isEmpty(); }
    public List<CartItem> getItems() { return new ArrayList<>(items.values()); }
    public Restaurant getRestaurant() { return restaurant; }
    public String getUserId() { return userId; }
}
