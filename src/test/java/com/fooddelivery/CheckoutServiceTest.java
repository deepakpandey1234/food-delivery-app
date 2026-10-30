package com.fooddelivery;

import com.fooddelivery.exception.CartException;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** End-to-end flows across Cart, Order, Payment, Delivery and notifications. */
class CheckoutServiceTest {
    private TestFixtures f;

    @BeforeEach
    void setUp() {
        f = new TestFixtures();
        f.cartService.addItem("U1", "R1", "F1", 2);  // 500
        f.cartService.addItem("U1", "R1", "F2", 1);  // 200
    }

    @Test
    void upiCheckoutConfirmsOrderAndClearsCart() {
        Order o = f.checkout.checkout(f.user, f.home, PaymentType.UPI);
        assertEquals(OrderStatus.CONFIRMED, o.getStatus());
        assertEquals(700.0, o.getAmount());
        assertEquals(PaymentStatus.SUCCESS, f.paymentService.find(o.getId()).orElseThrow().getStatus());
        assertTrue(f.cartService.getCart("U1").isEmpty());
    }

    @Test
    void failedPaymentCancelsOrderAndKeepsCart() {
        f.gatewayWorks = false;
        Order o = f.checkout.checkout(f.user, f.home, PaymentType.CARD);
        assertEquals(OrderStatus.CANCELLED, o.getStatus());
        assertFalse(f.cartService.getCart("U1").isEmpty());
    }

    @Test
    void codFullFlowCollectsCashOnDelivery() {
        Order o = f.checkout.checkout(f.user, f.home, PaymentType.COD);
        assertEquals(OrderStatus.CONFIRMED, o.getStatus());
        assertEquals(PaymentStatus.PENDING, f.paymentService.find(o.getId()).orElseThrow().getStatus());

        f.orderService.startPreparing(o.getId());
        f.orderService.pickUp(o.getId());
        f.orderService.deliver(o.getId());

        assertEquals(OrderStatus.DELIVERED, o.getStatus());
        assertEquals(PaymentStatus.SUCCESS, f.paymentService.find(o.getId()).orElseThrow().getStatus());
    }

    @Test
    void cancellingPaidOrderTriggersRefund() {
        Order o = f.checkout.checkout(f.user, f.home, PaymentType.UPI);
        f.orderService.cancel(o.getId());
        assertEquals(PaymentStatus.REFUNDED, f.paymentService.find(o.getId()).orElseThrow().getStatus());
        assertTrue(f.notifications.getSent().stream()
                .anyMatch(m -> m.startsWith("[RESTAURANT:R1]") && m.endsWith("CANCELLED")));
    }

    @Test
    void emptyCartCannotCheckout() {
        f.cartService.clear("U1");
        assertThrows(CartException.class, () -> f.checkout.checkout(f.user, f.home, PaymentType.UPI));
    }

    @Test
    void userIsNotifiedAtEveryStep() {
        Order o = f.checkout.checkout(f.user, f.home, PaymentType.UPI);
        f.orderService.startPreparing(o.getId());
        f.orderService.pickUp(o.getId());
        f.orderService.deliver(o.getId());
        long userMsgs = f.notifications.getSent().stream().filter(m -> m.startsWith("[USER:U1]")).count();
        assertEquals(4, userMsgs); // CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED
    }
}
