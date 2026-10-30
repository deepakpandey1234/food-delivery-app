package com.fooddelivery.model;

import com.fooddelivery.observer.OrderObserver;
import com.fooddelivery.state.OrderState;
import com.fooddelivery.state.PlacedState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * State-pattern CONTEXT and Observer-pattern SUBJECT.
 * Callers only invoke named actions; the current state decides if the move is legal.
 */
public class Order {
    private final String id;
    private final User user;
    private final Restaurant restaurant;
    private final Address deliveryAddress;
    private final List<CartItem> items;
    private final double amount;
    private volatile PaymentType paymentType;
    private volatile DeliveryPartner deliveryPartner;
    private OrderState state = PlacedState.INSTANCE;
    private final List<OrderObserver> observers = new CopyOnWriteArrayList<>();

    public Order(String id, User user, Restaurant restaurant, Address deliveryAddress, List<CartItem> items) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.items = List.copyOf(items);
        this.amount = this.items.stream().mapToDouble(CartItem::total).sum();
    }

    // ---- named actions (delegated to current state) ----
    public synchronized void confirm()        { state.confirm(this); }
    public synchronized void startPreparing() { state.startPreparing(this); }
    public synchronized void pickUp()         { state.pickUp(this); }
    public synchronized void deliver()        { state.deliver(this); }
    public synchronized void cancel()         { state.cancel(this); }

    /** Called only by state classes. */
    public void transitionTo(OrderState next) {
        this.state = next;
        for (OrderObserver o : observers) {
            o.onStatusChange(this, next.getStatus());
        }
    }

    public void addObserver(OrderObserver o) { observers.add(o); }
    public void removeObserver(OrderObserver o) { observers.remove(o); }

    public synchronized OrderStatus getStatus() { return state.getStatus(); }
    public String getId() { return id; }
    public User getUser() { return user; }
    public Restaurant getRestaurant() { return restaurant; }
    public Address getDeliveryAddress() { return deliveryAddress; }
    public List<CartItem> getItems() { return items; }
    public double getAmount() { return amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public DeliveryPartner getDeliveryPartner() { return deliveryPartner; }
    public void setDeliveryPartner(DeliveryPartner p) { this.deliveryPartner = p; }
}
