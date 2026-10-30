package com.fooddelivery.service;

import com.fooddelivery.model.Address;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;
import com.fooddelivery.model.User;
import com.fooddelivery.payment.PaymentFactory;
import com.fooddelivery.payment.PaymentService;
import com.fooddelivery.payment.PaymentStrategy;

/**
 * Orchestrator: OrderService and PaymentService never call each other.
 * Production version: event-driven (PaymentSucceeded event -> OrderService.confirm),
 * plus a timeout job that cancels PLACED orders with no payment after ~15 min.
 */
public class CheckoutService {
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PaymentFactory paymentFactory;

    public CheckoutService(CartService cs, OrderService os, PaymentService ps, PaymentFactory pf) {
        this.cartService = cs;
        this.orderService = os;
        this.paymentService = ps;
        this.paymentFactory = pf;
    }

    public Order checkout(User user, Address address, PaymentType type) {
        Cart cart = cartService.getCart(user.getId());

        Order order = orderService.placeOrder(user, cart, address);           // 1. PLACED
        order.setPaymentType(type);

        PaymentStrategy strategy = paymentFactory.get(type);                    // 2. pick strategy
        PaymentStatus ps = paymentService.processPayment(order.getId(), order.getAmount(), strategy); // 3. pay

        if (ps == PaymentStatus.SUCCESS || ps == PaymentStatus.PENDING) {       // 4. move order
            orderService.confirm(order.getId());                                //    PLACED -> CONFIRMED
            cartService.clear(user.getId());                                    //    clear only on success
        } else {
            orderService.cancel(order.getId());                                 //    keep cart for retry
        }
        return order;
    }
}
