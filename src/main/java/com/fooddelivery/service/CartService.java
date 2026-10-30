package com.fooddelivery.service;

import com.fooddelivery.exception.CartException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.Restaurant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CartService {
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private final RestaurantService restaurantService;

    public CartService(RestaurantService restaurantService) { this.restaurantService = restaurantService; }

    public Cart getCart(String userId) { return carts.computeIfAbsent(userId, Cart::new); }

    public Cart addItem(String userId, String restaurantId, String itemId, int qty) {
        Restaurant r = restaurantService.find(restaurantId)
                .orElseThrow(() -> new CartException("Restaurant not found: " + restaurantId));
        if (!r.isOpen()) throw new CartException(r.getName() + " is closed");
        FoodItem item = r.findItem(itemId)
                .orElseThrow(() -> new CartException("Item not found: " + itemId));
        if (!item.isAvailable()) throw new CartException(item.getName() + " is not available");

        Cart cart = getCart(userId);
        synchronized (cart) {
            if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(restaurantId)) {
                throw new CartException("Cart has items from " + cart.getRestaurant().getName()
                        + ". Clear the cart to order from " + r.getName());
            }
            cart.put(new CartItem(item.getId(), item.getName(), item.getPrice(), qty), r);
        }
        return cart;
    }

    public void removeItem(String userId, String itemId) {
        Cart cart = getCart(userId);
        synchronized (cart) { cart.remove(itemId); }
    }

    public void clear(String userId) {
        Cart cart = getCart(userId);
        synchronized (cart) { cart.clear(); }
    }
}
