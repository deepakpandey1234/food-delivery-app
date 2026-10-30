package com.fooddelivery.model;

public class FoodItem {
    private final String id;
    private final String name;
    private final double price;
    private final FoodType type;
    private volatile boolean available = true;

    public FoodItem(String id, String name, double price, FoodType type) {
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public FoodType getType() { return type; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
