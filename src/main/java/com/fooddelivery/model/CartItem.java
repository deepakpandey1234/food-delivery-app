package com.fooddelivery.model;

/**
 * Immutable line item. Price is captured at the moment it is added, so a later
 * menu price change does not silently change what the user agreed to pay.
 */
public record CartItem(String itemId, String name, double unitPrice, int quantity) {
    public CartItem {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
    }
    public double total() { return unitPrice * quantity; }
    public CartItem withQuantity(int q) { return new CartItem(itemId, name, unitPrice, q); }
}
