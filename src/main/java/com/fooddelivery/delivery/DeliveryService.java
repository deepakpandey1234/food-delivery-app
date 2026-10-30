package com.fooddelivery.delivery;

import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Order;

import java.util.List;
import java.util.Optional;

public class DeliveryService {
    private static final double[] SEARCH_RADII_KM = {3, 5, 8};

    private volatile AssignmentStrategy strategy;
    private final PartnerRepository repo;

    public DeliveryService(AssignmentStrategy strategy, PartnerRepository repo) {
        this.strategy = strategy;
        this.repo = repo;
    }

    /** Switch rule at runtime, e.g. LeastBusy during peak hours. */
    public void setStrategy(AssignmentStrategy strategy) { this.strategy = strategy; }

    /**
     * No global lock. Each partner is claimed with an atomic compare-and-set, so two
     * orders racing for the same partner can never both win; the loser tries the next one.
     * Radius widens if nobody is free nearby.
     */
    public Optional<DeliveryPartner> assign(Order order) {
        for (double radius : SEARCH_RADII_KM) {
            List<DeliveryPartner> ranked =
                    strategy.rank(order, repo.findNearby(order.getRestaurant().getLocation(), radius));
            for (DeliveryPartner p : ranked) {
                if (p.tryClaim()) {
                    order.setDeliveryPartner(p);
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty(); // caller / scheduler retries later
    }

    public void release(Order order, boolean completed) {
        DeliveryPartner p = order.getDeliveryPartner();
        if (p != null) p.release(completed);
    }
}
