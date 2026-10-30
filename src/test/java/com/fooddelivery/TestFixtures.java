package com.fooddelivery;

import com.fooddelivery.delivery.DeliveryService;
import com.fooddelivery.delivery.NearestPartnerStrategy;
import com.fooddelivery.delivery.PartnerRepository;
import com.fooddelivery.model.*;
import com.fooddelivery.observer.*;
import com.fooddelivery.payment.*;
import com.fooddelivery.service.*;

import java.util.List;

/** Wires the whole app in memory for tests. gatewayWorks toggles UPI/card success. */
public class TestFixtures {
    public final NotificationService notifications = new NotificationService();
    public final RestaurantService restaurantService = new RestaurantService();
    public final CartService cartService = new CartService(restaurantService);
    public final PaymentService paymentService = new PaymentService();
    public final PartnerRepository partners = new PartnerRepository();
    public final DeliveryService deliveryService = new DeliveryService(new NearestPartnerStrategy(), partners);
    public final OrderService orderService;
    public final CheckoutService checkout;
    public volatile boolean gatewayWorks = true;

    public final Restaurant restaurant = new Restaurant("R1", "Tunday Kababi", new Location(26.8550, 80.9450));
    public final Restaurant otherRestaurant = new Restaurant("R2", "Royal Cafe", new Location(26.8520, 80.9470));
    public final FoodItem kebab = new FoodItem("F1", "Galouti Kebab", 250, FoodType.NON_VEG);
    public final FoodItem paneer = new FoodItem("F2", "Paneer Tikka", 200, FoodType.VEG);
    public final FoodItem chaat = new FoodItem("F3", "Basket Chaat", 120, FoodType.VEG);
    public final User user = new User("U1", "Deepak", "9999999999");
    public final Address home = new Address("A1", "Home", "Gomti Nagar", new Location(26.8600, 80.9900));
    public final DeliveryPartner partner = new DeliveryPartner("P1", "Ravi", new Location(26.8560, 80.9460));

    public TestFixtures() {
        orderService = new OrderService(List.of(
                new UserNotifier(notifications),
                new RestaurantNotifier(notifications),
                new PaymentObserver(paymentService),
                new DeliveryAssignmentObserver(deliveryService)));
        PaymentGateway gateway = (key, amount) -> gatewayWorks;
        PaymentFactory factory = new PaymentFactory(List.of(
                new UpiPayment(gateway), new CardPayment(gateway), new CashOnDeliveryPayment()));
        checkout = new CheckoutService(cartService, orderService, paymentService, factory);

        restaurant.addMenuItem(kebab);
        restaurant.addMenuItem(paneer);
        otherRestaurant.addMenuItem(chaat);
        restaurantService.register(restaurant);
        restaurantService.register(otherRestaurant);
        user.addAddress(home);
        partners.add(partner);
    }

    public Order placedOrder() {
        cartService.addItem(user.getId(), "R1", "F1", 1);
        return orderService.placeOrder(user, cartService.getCart(user.getId()), home);
    }
}
