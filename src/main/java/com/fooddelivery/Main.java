package com.fooddelivery;

import com.fooddelivery.delivery.DeliveryService;
import com.fooddelivery.delivery.NearestPartnerStrategy;
import com.fooddelivery.delivery.PartnerRepository;
import com.fooddelivery.model.*;
import com.fooddelivery.observer.*;
import com.fooddelivery.payment.*;
import com.fooddelivery.service.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // ---- wiring (what Spring would do) ----
        NotificationService notifications = new NotificationService();
        RestaurantService restaurantService = new RestaurantService();
        CartService cartService = new CartService(restaurantService);
        PaymentService paymentService = new PaymentService();
        PartnerRepository partners = new PartnerRepository();
        DeliveryService deliveryService = new DeliveryService(new NearestPartnerStrategy(), partners);

        OrderService orderService = new OrderService(List.of(
                new UserNotifier(notifications),
                new RestaurantNotifier(notifications),
                new PaymentObserver(paymentService),
                new DeliveryAssignmentObserver(deliveryService)));

        PaymentGateway fakeGateway = (key, amount) -> true;
        PaymentFactory paymentFactory = new PaymentFactory(List.of(
                new UpiPayment(fakeGateway), new CardPayment(fakeGateway), new CashOnDeliveryPayment()));
        CheckoutService checkout = new CheckoutService(cartService, orderService, paymentService, paymentFactory);

        // ---- data ----
        Location hazratganj = new Location(26.8500, 80.9490);
        Restaurant r = new Restaurant("R1", "Tunday Kababi", new Location(26.8550, 80.9450));
        r.addMenuItem(new FoodItem("F1", "Galouti Kebab", 250, FoodType.NON_VEG));
        r.addMenuItem(new FoodItem("F2", "Paneer Tikka", 220, FoodType.VEG));
        restaurantService.register(r);
        partners.add(new DeliveryPartner("P1", "Ravi", new Location(26.8560, 80.9460)));

        User deepak = new User("U1", "Deepak", "9999999999");
        Address home = new Address("A1", "Home", "Gomti Nagar", new Location(26.8600, 80.9900));
        Address office = new Address("A2", "Office", "Hazratganj", hazratganj);
        deepak.addAddress(home);
        deepak.addAddress(office);

        // ---- flow ----
        System.out.println("Nearby: " + restaurantService.listNearby(hazratganj, 5).stream().map(Restaurant::getName).toList());
        cartService.addItem("U1", "R1", "F1", 2);
        cartService.addItem("U1", "R1", "F2", 1);
        Address deliverTo = deepak.nearestAddress(hazratganj).orElseThrow();

        Order order = checkout.checkout(deepak, deliverTo, PaymentType.COD);
        System.out.println("Amount: ₹" + order.getAmount() + ", payment: " + paymentService.find(order.getId()).orElseThrow().getStatus());

        orderService.startPreparing(order.getId());   // restaurant accepts -> partner auto-assigned
        System.out.println("Partner: " + order.getDeliveryPartner().getName());
        orderService.pickUp(order.getId());
        orderService.deliver(order.getId());
        System.out.println("Final: " + order.getStatus() + ", payment: " + paymentService.find(order.getId()).orElseThrow().getStatus());
    }
}
