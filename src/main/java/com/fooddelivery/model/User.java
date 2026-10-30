package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class User {
    private final String id;
    private final String name;
    private final String phone;
    private final List<Address> addresses = new ArrayList<>();
    private FoodPreference preference = FoodPreference.BOTH;

    public User(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public void addAddress(Address address) { addresses.add(address); }

    /** Default address = the one nearest to the user's current GPS location. */
    public Optional<Address> nearestAddress(Location current) {
        return addresses.stream()
                .min((a, b) -> Double.compare(a.location().distanceTo(current), b.location().distanceTo(current)));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<Address> getAddresses() { return Collections.unmodifiableList(addresses); }
    public FoodPreference getPreference() { return preference; }
    public void setPreference(FoodPreference preference) { this.preference = preference; }
}
