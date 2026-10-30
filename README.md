# Food Delivery App — Low Level Design (Java 17)

Run tests: `mvn test`  ·  Run demo: `mvn compile exec:java`

## Package map
| Package | What | Pattern |
|---|---|---|
| `model` | User, Address, Restaurant, FoodItem, Cart, CartItem, Order, Payment, DeliveryPartner, enums | — |
| `state` | PlacedState → ConfirmedState → PreparingState → OutForDeliveryState → DeliveredState / CancelledState | **State** (singleton states) |
| `observer` | UserNotifier, RestaurantNotifier, PaymentObserver, DeliveryAssignmentObserver | **Observer** |
| `payment` | UpiPayment, CardPayment, CashOnDeliveryPayment, PaymentFactory, PaymentService | **Strategy + Factory** |
| `delivery` | NearestPartnerStrategy, LeastBusyStrategy, DeliveryService | **Strategy** |
| `service` | RestaurantService, CartService, OrderService, CheckoutService | Orchestrator |

## Flow
```
listNearby -> addItem (single restaurant) -> checkout
  CheckoutService: placeOrder(PLACED) -> PaymentFactory.get(type) -> processPayment
     SUCCESS/PENDING -> confirm (CONFIRMED), clear cart
     FAILED          -> cancel, keep cart
Restaurant: startPreparing -> DeliveryAssignmentObserver assigns partner
Partner:    pickUp -> deliver -> PaymentObserver marks COD collected, partner released
Cancel (PLACED/CONFIRMED only) -> PaymentObserver refunds
```

## Talking points
- Named actions (`confirm/startPreparing/pickUp/deliver/cancel`), not `next()` — a pickup event can't confirm.
- Order status and payment status are separate (COD = CONFIRMED + PENDING).
- Payment is idempotent on orderId — retries never double-charge; FAILED can be retried.
- Partner claim is an atomic compare-and-set (= `UPDATE ... WHERE available = true`), no global lock. Radius widens 3 → 5 → 8 km.
- Price is captured into CartItem when added.
- Production: replace direct observer calls with Kafka events; Redis GEO for partner search.

## Not covered (discuss as extensions)
- Partner accept/reject with timeout and re-assignment
- Payment timeout job cancelling unpaid PLACED orders
- Restaurant rejecting an order; item stock/quantity limits
- Live location tracking and ETA; ratings, coupons, delivery fee and taxes
- Persistence/repositories and optimistic locking across multiple servers
