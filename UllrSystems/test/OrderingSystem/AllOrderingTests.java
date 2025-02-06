package OrderingSystem;

import Databasing.SQLInterfacing;
import Food.FoodItem;
import Fridge.Fridge;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Combined ordering system tests:
 * - Order model tests
 * - SQLInterfacing (ordering-related DB operations) tests
 * - A sample servlet test (GetAllFridgesServlet)
 *
 * Timeout annotations have been added so that if a test fails (e.g. due to a connection issue)
 * it will not loop indefinitely.
 */
public class AllOrderingTests {

    // ============================================
    // 1. Order Model Tests (no DB connection, so no timeout needed)
    // ============================================
    @Test
    public void testOrderProperties() {
        Order order = new Order();

        // Set order properties
        order.SetOrderId(101);
        order.SetOrderDate(LocalDate.of(2025, 1, 1));
        order.SetDeliveryDate(LocalDate.of(2025, 2, 1));

        // Prepare dummy FoodItems
        FoodItem item1 = new FoodItem();
        item1.SetFoodID(1);
        item1.SetFoodName("Bananas");
        item1.SetWeight(2.5);
        item1.SetExpirationDate(LocalDate.of(2025, 3, 15));

        FoodItem item2 = new FoodItem();
        item2.SetFoodID(2);
        item2.SetFoodName("Apples");
        item2.SetWeight(3.0);
        item2.SetExpirationDate(LocalDate.of(2025, 3, 20));

        ArrayList<FoodItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        order.SetFood(items);

        // Verify getters
        assertEquals("OrderId should be 101.", 101, order.GetOrderId());
        assertEquals("Order date should be 2025-01-01.", LocalDate.of(2025, 1, 1), order.GetOrderDate());
        assertEquals("Delivery date should be 2025-02-01.", LocalDate.of(2025, 2, 1), order.GetDeliveryDate());
        assertNotNull("Food list should not be null.", order.GetFood());
        assertEquals("Food list size should be 2.", 2, order.GetFood().size());
        assertEquals("First item name should be Bananas.", "Bananas", order.GetFood().get(0).GetFoodName());
        assertEquals("Second item ID should be 2.", 2, order.GetFood().get(1).GetFoodID());
    }

    @Test
    public void testOrderToString() {
        Order order = new Order();
        order.SetOrderId(999);
        order.SetOrderDate(LocalDate.of(2025, 5, 5));
        order.SetDeliveryDate(LocalDate.of(2025, 6, 6));

        String stringRepresentation = order.toString();
        assertNotNull("Order toString() should not be null.", stringRepresentation);
        assertTrue("Order toString() should contain 'orderId'.", stringRepresentation.contains("orderId"));
        assertTrue("Order toString() should contain '999'.", stringRepresentation.contains("999"));
    }

    // ============================================
    // 2. SQLInterfacing (Database) Ordering Tests
    // ============================================
    // Add a timeout (5000 ms) to prevent tests from looping indefinitely if connection fails.
    private final SQLInterfacing sql = new SQLInterfacing();

    @Test(timeout = 5000)
    public void testGetAllFridges() {
        try {
            ArrayList<Fridge> fridgeList = sql.GetAllFridges();
            assertNotNull("Fridge list should not be null.", fridgeList);
            // If you expect at least one fridge, you could add:
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
            // Optionally, if you expect data:
            // assertFalse("Available food list should not be empty.", foods.isEmpty());
        } catch (SQLException e) {
            fail("testGetAllAvailableFoodForOrder threw SQLException: " + e.getMessage());
        }
    }

    /**
     * This test attempts to add an order to the database.
     * Note: Since AddOrderToDB returns a boolean, we verify that the insertion succeeded.
     */
    @Test(timeout = 5000)
    public void testAddAndGetOrderFromDB() {
        try {
            // Create a sample order
            Order order = new Order();
            order.SetOrderDate(LocalDate.now());
            order.SetDeliveryDate(LocalDate.now().plusDays(7));

            // Create dummy FoodItems
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

            // Insert order into DB
            boolean insertSuccess = sql.AddOrderToDB(order);
            assertTrue("AddOrderToDB should return true on success.", insertSuccess);

            // Retrieval test:
            // Since your current implementation does not return the generated orderId,
            // we only check that no exception was thrown.
        } catch (SQLException e) {
            fail("testAddAndGetOrderFromDB threw SQLException: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testDeleteFoodFromSupplier() {
        try {
            // Test deletion of a food item with a given test foodId.
            // Adjust testFoodId as needed; if the item doesn't exist, the method should still not throw.
            int testFoodId = 99999;
            sql.DeleteFoodFromSupplier(testFoodId);
            // If no exception is thrown, consider it passed.
        } catch (SQLException e) {
            fail("testDeleteFoodFromSupplier threw SQLException: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testGetAllOrdersForHistory() {
        try {
            ArrayList<Order> orders = sql.GetAllOrdersForHistory();
            assertNotNull("Order history list should not be null.", orders);
        } catch (SQLException e) {
            fail("testGetAllOrdersForHistory threw SQLException: " + e.getMessage());
        }
    }

    // ============================================
    // 3. Sample Servlet Test (GetAllFridgesServlet)
    // ============================================
    @Test(timeout = 5000)
    public void testGetAllFridgesServlet() throws ServletException, IOException {
        // Prepare mock HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Capture the servlet's output
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Instantiate and call the servlet's doGet method
        GetAllFridgesServlet servlet = new GetAllFridgesServlet();
        servlet.doGet(request, response);

        // Flush the writer and capture the result
        printWriter.flush();
        String result = stringWriter.toString();
        assertNotNull("Servlet output should not be null.", result);
        assertTrue("Response should contain 'fridges' key.", result.contains("\"fridges\""));
    }
}
