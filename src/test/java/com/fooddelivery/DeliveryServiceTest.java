package com.fooddelivery;

import com.fooddelivery.delivery.DeliveryService;
import com.fooddelivery.delivery.LeastBusyStrategy;
import com.fooddelivery.delivery.NearestPartnerStrategy;
import com.fooddelivery.delivery.PartnerRepository;
import com.fooddelivery.model.DeliveryPartner;
import com.fooddelivery.model.Location;
import com.fooddelivery.model.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {

    private Order order(TestFixtures f) { return f.placedOrder(); }

    @Test
    void nearestStrategyPicksClosestPartner() {
        TestFixtures f = new TestFixtures();
        PartnerRepository repo = new PartnerRepository();
        DeliveryPartner near = new DeliveryPartner("N", "Near", new Location(26.8551, 80.9451));
        DeliveryPartner far = new DeliveryPartner("F", "Far", new Location(26.8700, 80.9600));
        repo.add(far);
        repo.add(near);
        DeliveryService ds = new DeliveryService(new NearestPartnerStrategy(), repo);
        assertEquals("N", ds.assign(order(f)).orElseThrow().getId());
        assertFalse(near.isAvailable());
    }

    @Test
    void leastBusyStrategyPicksPartnerWithFewestDeliveries() {
        TestFixtures f = new TestFixtures();
        PartnerRepository repo = new PartnerRepository();
        DeliveryPartner busy = new DeliveryPartner("B", "Busy", new Location(26.8551, 80.9451));
        DeliveryPartner idle = new DeliveryPartner("I", "Idle", new Location(26.8600, 80.9500));
        busy.tryClaim(); busy.release(true); busy.tryClaim(); busy.release(true);
        repo.add(busy);
        repo.add(idle);
        DeliveryService ds = new DeliveryService(new LeastBusyStrategy(), repo);
        assertEquals("I", ds.assign(order(f)).orElseThrow().getId());
    }

    @Test
    void noPartnerAvailableReturnsEmpty() {
        TestFixtures f = new TestFixtures();
        DeliveryService ds = new DeliveryService(new NearestPartnerStrategy(), new PartnerRepository());
        assertEquals(Optional.empty(), ds.assign(order(f)));
    }

    @Test
    void searchRadiusWidensWhenNobodyIsClose() {
        TestFixtures f = new TestFixtures();
        PartnerRepository repo = new PartnerRepository();
        DeliveryPartner sixKmAway = new DeliveryPartner("X", "X", new Location(26.9090, 80.9450)); // ~6 km north
        repo.add(sixKmAway);
        DeliveryService ds = new DeliveryService(new NearestPartnerStrategy(), repo);
        assertTrue(ds.assign(order(f)).isPresent());
    }

    @Test
    void partnerIsAssignedAutomaticallyWhenPreparingAndFreedOnDelivery() {
        TestFixtures f = new TestFixtures();
        Order o = order(f);
        o.confirm();
        o.startPreparing();
        assertSame(f.partner, o.getDeliveryPartner());
        assertFalse(f.partner.isAvailable());
        o.pickUp();
        o.deliver();
        assertTrue(f.partner.isAvailable());
        assertEquals(1, f.partner.getCompletedToday());
    }

    @Test
    void concurrentOrdersNeverGetTheSamePartner() throws Exception {
        TestFixtures f = new TestFixtures();
        PartnerRepository repo = new PartnerRepository();
        int partners = 5, orders = 50;
        for (int i = 0; i < partners; i++) {
            repo.add(new DeliveryPartner("P" + i, "P" + i, new Location(26.8551 + i * 0.0001, 80.9451)));
        }
        DeliveryService ds = new DeliveryService(new NearestPartnerStrategy(), repo);

        List<Order> list = new ArrayList<>();
        for (int i = 0; i < orders; i++) list.add(order(f));

        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Optional<DeliveryPartner>>> results = new ArrayList<>();
        for (Order o : list) {
            results.add(pool.submit(() -> { start.await(); return ds.assign(o); }));
        }
        start.countDown();

        Set<String> assigned = ConcurrentHashMap.newKeySet();
        int successes = 0;
        for (Future<Optional<DeliveryPartner>> r : results) {
            Optional<DeliveryPartner> p = r.get(5, TimeUnit.SECONDS);
            if (p.isPresent()) {
                successes++;
                assertTrue(assigned.add(p.get().getId()), "partner double-booked: " + p.get().getId());
            }
        }
        pool.shutdown();
        assertEquals(partners, successes);
    }
}
