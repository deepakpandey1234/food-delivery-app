package com.fooddelivery.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryPartner {
    private final String id;
    private final String name;
    private volatile Location location;
    private final AtomicBoolean available = new AtomicBoolean(true);
    private final AtomicInteger completedToday = new AtomicInteger();

    public DeliveryPartner(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    /**
     * Atomic claim: only ONE thread can flip available true -> false.
     * Same idea as SQL: UPDATE partner SET available=false WHERE id=? AND available=true
     */
    public boolean tryClaim() { return available.compareAndSet(true, false); }

    public void release(boolean completed) {
        if (completed) completedToday.incrementAndGet();
        available.set(true);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public boolean isAvailable() { return available.get(); }
    public int getCompletedToday() { return completedToday.get(); }
}
