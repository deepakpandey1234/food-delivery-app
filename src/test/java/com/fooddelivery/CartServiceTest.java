package com.fooddelivery;

import com.fooddelivery.exception.CartException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.FoodPreference;
import com.fooddelivery.model.Location;
import com.fooddelivery.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {
    private TestFixtures f;

    @BeforeEach
    void setUp() { f = new TestFixtures(); }

    @Test
    void addingSameItemTwiceMergesQuantity() {
        f.cartService.addItem("U1", "R1", "F1", 1);
        Cart cart = f.cartService.addItem("U1", "R1", "F1", 2);
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).quantity());
        assertEquals(750.0, cart.total());
    }

    @Test
    void cartAllowsOnlyOneRestaurant() {
        f.cartService.addItem("U1", "R1", "F1", 1);
        CartException ex = assertThrows(CartException.class,
                () -> f.cartService.addItem("U1", "R2", "F3", 1));
        assertTrue(ex.getMessage().contains("Clear the cart"));
    }

    @Test
    void afterClearingUserCanSwitchRestaurant() {
        f.cartService.addItem("U1", "R1", "F1", 1);
        f.cartService.clear("U1");
        Cart cart = f.cartService.addItem("U1", "R2", "F3", 1);
        assertEquals("R2", cart.getRestaurant().getId());
    }

    @Test
    void unavailableItemCannotBeAdded() {
        f.kebab.setAvailable(false);
        assertThrows(CartException.class, () -> f.cartService.addItem("U1", "R1", "F1", 1));
    }

    @Test
    void closedRestaurantCannotTakeItems() {
        f.restaurant.setOpen(false);
        assertThrows(CartException.class, () -> f.cartService.addItem("U1", "R1", "F1", 1));
    }

    @Test
    void priceIsCapturedWhenAdded() {
        Cart cart = f.cartService.addItem("U1", "R1", "F2", 1);
        assertEquals(200.0, cart.getItems().get(0).unitPrice());
    }

    @Test
    void vegPreferenceFiltersMenu() {
        List<FoodItem> menu = f.restaurantService.menuFor("R1", FoodPreference.VEG);
        assertEquals(1, menu.size());
        assertEquals("F2", menu.get(0).getId());
        assertEquals(2, f.restaurantService.menuFor("R1", FoodPreference.BOTH).size());
    }

    @Test
    void nearbyListingSkipsFarAndClosedRestaurants() {
        Restaurant far = new Restaurant("R3", "Far Dhaba", new Location(28.6139, 77.2090)); // Delhi
        f.restaurantService.register(far);
        f.otherRestaurant.setOpen(false);
        List<Restaurant> nearby = f.restaurantService.listNearby(new Location(26.85, 80.949), 5);
        assertEquals(1, nearby.size());
        assertEquals("R1", nearby.get(0).getId());
    }

    @Test
    void nearestAddressIsChosenByDefault() {
        var office = new com.fooddelivery.model.Address("A2", "Office", "Hazratganj", new Location(26.85, 80.949));
        f.user.addAddress(office);
        assertEquals("A2", f.user.nearestAddress(new Location(26.851, 80.95)).orElseThrow().id());
    }
}
