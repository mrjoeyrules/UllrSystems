package OrderingSystem;

import Databasing.SQLInterfacing;
import Food.FoodItem;
import Fridge.Fridge;
import OrderingSystem.Order;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Combined ordering system tests for the Order model and SQLInterfacing functions.
 */
public class AllOrderingTests {

    // ============================================
    // 1. Order Model Tests (no DB connection needed)
    // ============================================
    
    @Test
    public void testOrderProperties() {
        Order order = new Order();
        order.SetOrderId(101);
        order.SetOrderDate(LocalDate.of(2025, 1, 1));
        order.SetDeliveryDate(LocalDate.of(2025, 2, 1));

        FoodItem item1 = new FoodItem();
        item1.SetFoodID(1);
        item1.SetFoodName("Bananas");

        FoodItem item2 = new FoodItem();
        item2.SetFoodID(2);
        item2.SetFoodName("Apples");

        ArrayList<FoodItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        order.SetFood(items);

        assertEquals("OrderId should be 101.", 101, order.GetOrderId());
        assertEquals("OrderDate should be 2025-01-01.",
                     LocalDate.of(2025, 1, 1), order.GetOrderDate());
        assertEquals("DeliveryDate should be 2025-02-01.",
                     LocalDate.of(2025, 2, 1), order.GetDeliveryDate());
        assertEquals("Food list size should be 2.", 2, order.GetFood().size());
    }

    @Test
    public void testOrderToString() {
        Order order = new Order();
        order.SetOrderId(999);
        order.SetOrderDate(LocalDate.of(2025, 5, 5));
        order.SetDeliveryDate(LocalDate.of(2025, 6, 6));
        // Ensure foodList is not null so that toString() won't throw a NullPointerException.
        order.SetFood(new ArrayList<FoodItem>());
        
        String stringRepresentation = order.toString();
        assertNotNull("Order toString() should not be null.", stringRepresentation);
        assertTrue("Order toString() should contain 'orderId'.", 
                   stringRepresentation.contains("orderId"));
        assertTrue("Order toString() should contain '999'.", 
                   stringRepresentation.contains("999"));
    }

    // ============================================
    // 2. SQLInterfacing (Database) Ordering Tests
    // ============================================
    // Each test is annotated with a timeout of 5000 ms.
    private final SQLInterfacing sql = new SQLInterfacing();

    @Test(timeout = 5000)
    public void testGetAllFridges() {
        try {
            ArrayList<Fridge> fridgeList = sql.GetAllFridges();
            assertNotNull("Fridge list should not be null.", fridgeList);
            // Optionally, if you expect at least one fridge, uncomment:
            // assertFalse("Fridge list should not be empty.", fridgeList.isEmpty());
        } catch (SQLException e) {
            fail("testGetAllFridges threw SQLException: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testGetAllAvailableFoodForOrder() {
        try {
            ArrayList<FoodItem> foods = sql.GetAllAvailableFoodForOrder();
            assertNotNull("Available food list should not be null.", foods);
        } catch (SQLException e) {
            fail("testGetAllAvailableFoodForOrder threw SQLException: " + e.getMessage());
        }
    }

    /**
     * This test attempts to add an order to the database.
     * Since AddOrderToDB returns a boolean, we check that the insertion succeeded.
     */
    @Test(timeout = 5000)
    public void testAddAndGetOrderFromDB() {
        try {
            Order order = new Order();
            order.SetOrderDate(LocalDate.now());
            order.SetDeliveryDate(LocalDate.now().plusDays(7));

            FoodItem f1 = new FoodItem();
            f1.SetFoodID(99999);  // Use test IDs
            f1.SetFoodName("Test Food A");
            f1.SetWeight(1.0);
            f1.SetExpirationDate(LocalDate.now().plusMonths(1));

            FoodItem f2 = new FoodItem();
            f2.SetFoodID(88888);
            f2.SetFoodName("Test Food B");
            f2.SetWeight(2.5);
            f2.SetExpirationDate(LocalDate.now().plusMonths(2));

            ArrayList<FoodItem> foodList = new ArrayList<>();
            foodList.add(f1);
            foodList.add(f2);
            order.SetFood(foodList);

            boolean insertSuccess = sql.AddOrderToDB(order);
            assertTrue("AddOrderToDB should return true on success.", insertSuccess);
        } catch (SQLException e) {
            fail("testAddAndGetOrderFromDB threw SQLException: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testDeleteFoodFromSupplier() {
        try {
            // Test deletion of a food item using a test foodId.
            int testFoodId = 99999;
            sql.DeleteFoodFromSupplier(testFoodId);
            // If no exception is thrown, the test passes.
        } catch (SQLException e) {
            fail("testDeleteFoodFromSupplier threw SQLException: " + e.getMessage());
        }
    }


    
    @Test(expected = IllegalArgumentException.class)
public void testInvalidDeliveryDate() {
    Order order = new Order();
    order.SetOrderDate(LocalDate.now());
    order.SetDeliveryDate(LocalDate.now().minusDays(1));
}

@Test(expected = IllegalArgumentException.class)
public void testOrderWithNoFood() {
    Order order = new Order();
    order.SetFood(new ArrayList<>()); // Empty list
        try {
            sql.AddOrderToDB(order); // Should validate and throw
        } catch (SQLException ex) {
            Logger.getLogger(AllOrderingTests.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    
    
}

