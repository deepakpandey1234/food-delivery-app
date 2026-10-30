package com.fooddelivery;

import com.fooddelivery.model.PaymentStatus;
import com.fooddelivery.model.PaymentType;
import com.fooddelivery.payment.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    @Test
    void factoryReturnsStrategyForEachType() {
        PaymentGateway gw = (k, a) -> true;
        PaymentFactory factory = new PaymentFactory(List.of(
                new UpiPayment(gw), new CardPayment(gw), new CashOnDeliveryPayment()));
        assertTrue(factory.get(PaymentType.UPI) instanceof UpiPayment);
        assertTrue(factory.get(PaymentType.CARD) instanceof CardPayment);
        assertTrue(factory.get(PaymentType.COD) instanceof CashOnDeliveryPayment);
    }

    @Test
    void factoryRejectsUnregisteredType() {
        PaymentFactory factory = new PaymentFactory(List.of(new CashOnDeliveryPayment()));
        assertThrows(IllegalArgumentException.class, () -> factory.get(PaymentType.UPI));
    }

    @Test
    void codIsPendingUpiIsSuccess() {
        PaymentService ps = new PaymentService();
        assertEquals(PaymentStatus.PENDING, ps.processPayment("O1", 100, new CashOnDeliveryPayment()));
        assertEquals(PaymentStatus.SUCCESS, ps.processPayment("O2", 100, new UpiPayment((k, a) -> true)));
        assertEquals(PaymentStatus.FAILED, ps.processPayment("O3", 100, new CardPayment((k, a) -> false)));
    }

    @Test
    void retryDoesNotChargeTwice() {
        AtomicInteger charges = new AtomicInteger();
        PaymentStrategy upi = new UpiPayment((k, a) -> { charges.incrementAndGet(); return true; });
        PaymentService ps = new PaymentService();
        ps.processPayment("O1", 100, upi);
        ps.processPayment("O1", 100, upi); // double click / retry
        assertEquals(1, charges.get());
    }

    @Test
    void failedPaymentCanBeRetried() {
        AtomicInteger attempt = new AtomicInteger();
        PaymentStrategy flaky = new UpiPayment((k, a) -> attempt.incrementAndGet() > 1);
        PaymentService ps = new PaymentService();
        assertEquals(PaymentStatus.FAILED, ps.processPayment("O1", 100, flaky));
        assertEquals(PaymentStatus.SUCCESS, ps.processPayment("O1", 100, flaky));
    }

    @Test
    void refundAndCollectRules() {
        PaymentService ps = new PaymentService();
        ps.processPayment("PAID", 100, new UpiPayment((k, a) -> true));
        ps.refundIfPaid("PAID");
        assertEquals(PaymentStatus.REFUNDED, ps.find("PAID").orElseThrow().getStatus());

        ps.processPayment("COD", 100, new CashOnDeliveryPayment());
        ps.markCollected("COD");
        assertEquals(PaymentStatus.SUCCESS, ps.find("COD").orElseThrow().getStatus());
    }
}
