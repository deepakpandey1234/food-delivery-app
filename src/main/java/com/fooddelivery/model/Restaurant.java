package com.fooddelivery.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Restaurant HAS-A menu (composition of FoodItems). */
public class Restaurant {
    private final String id;
    private final String name;
    private final Location location;
    private final Map<String, FoodItem> menu = new LinkedHashMap<>();
    private volatile boolean open = true;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void addMenuItem(FoodItem item) { menu.put(item.getId(), item); }
    public Optional<FoodItem> findItem(String itemId) { return Optional.ofNullable(menu.get(itemId)); }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public Collection<FoodItem> getMenu() { return Collections.unmodifiableCollection(menu.values()); }
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
}
