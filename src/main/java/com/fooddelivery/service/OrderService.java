package com.fooddelivery.service;

import com.fooddelivery.exception.CartException;
import com.fooddelivery.model.Address;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import com.fooddelivery.observer.OrderObserver;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Owns orders. Knows nothing about states: it only calls named actions on Order. */
public class OrderService {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final List<OrderObserver> defaultObservers;
    private final AtomicLong seq = new AtomicLong(100);

    public OrderService(List<OrderObserver> defaultObservers) {
        this.defaultObservers = List.copyOf(defaultObservers);
    }

    public Order placeOrder(User user, Cart cart, Address address) {
        if (cart.isEmpty()) throw new CartException("Cart is empty");
        if (!user.getAddresses().contains(address)) throw new IllegalArgumentException("Unknown address");
        String id = "ORD-" + seq.incrementAndGet();
        Order order = new Order(id, user, cart.getRestaurant(), address, cart.getItems());
        defaultObservers.forEach(order::addObserver);
        orders.put(id, order);
        return order; // status = PLACED
    }

    public void confirm(String id)        { get(id).confirm(); }
    public void startPreparing(String id) { get(id).startPreparing(); }  // restaurant app
    public void pickUp(String id)         { get(id).pickUp(); }          // partner app
    public void deliver(String id)        { get(id).deliver(); }         // partner app
    public void cancel(String id)         { get(id).cancel(); }          // user / system

    public Order get(String id) {
        Order o = orders.get(id);
        if (o == null) throw new NoSuchElementException("Order not found: " + id);
        return o;
    }
}
