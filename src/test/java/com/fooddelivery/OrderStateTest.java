package com.fooddelivery;

import com.fooddelivery.exception.InvalidOrderStateException;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateTest {
    private TestFixtures f;
    private Order order;

    @BeforeEach
    void setUp() {
        f = new TestFixtures();
        order = f.placedOrder();
    }

    @Test
    void newOrderStartsAsPlaced() {
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void happyPathMovesThroughEveryState() {
        order.confirm();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        order.startPreparing();
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        order.pickUp();
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus());
        order.deliver();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void canCancelWhenPlacedOrConfirmed() {
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        Order second = f.placedOrder();
        second.confirm();
        second.cancel();
        assertEquals(OrderStatus.CANCELLED, second.getStatus());
    }

    @Test
    void cannotCancelOncePreparing() {
        order.confirm();
        order.startPreparing();
        InvalidOrderStateException ex = assertThrows(InvalidOrderStateException.class, order::cancel);
        assertTrue(ex.getMessage().contains("PREPARING"));
        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void cannotSkipSteps() {
        assertThrows(InvalidOrderStateException.class, order::pickUp);   // pickup cannot confirm
        assertThrows(InvalidOrderStateException.class, order::deliver);
        assertThrows(InvalidOrderStateException.class, order::startPreparing);
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void terminalStatesRejectEverything() {
        order.cancel();
        assertThrows(InvalidOrderStateException.class, order::confirm);
        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void observersAreNotifiedOnEachChange() {
        int before = f.notifications.getSent().size();
        order.confirm(); // user + restaurant
        assertEquals(before + 2, f.notifications.getSent().size());
        order.startPreparing(); // user only
        assertEquals(before + 3, f.notifications.getSent().size());
    }

    @Test
    void failedTransitionDoesNotNotify() {
        int before = f.notifications.getSent().size();
        assertThrows(InvalidOrderStateException.class, order::deliver);
        assertEquals(before, f.notifications.getSent().size());
    }
}
