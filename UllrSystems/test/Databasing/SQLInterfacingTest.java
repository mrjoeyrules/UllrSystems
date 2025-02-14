/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Databasing;

import Accounts.User;
import Alerts.Alerts;
import Food.FoodItem;
import Fridge.Fridge;
import Inventory.Shelf;
import OrderingSystem.Order;
import Reports.Report;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class SQLInterfacingTest {

    private SQLInterfacing sql;
    private static final String TEST_USERNAME = "testUser01";
    private static final String TEST_PASSWORD = "testPass01";

    @Before
    public void setUp() {
        sql = new SQLInterfacing();
    }

    @After
    public void tearDown() {
        try {
            // Remove any test users
            sql.cleanupTestUsers();
            // Remove any test fridges
            sql.cleanupTestFridges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------
    // General SQL and Logging Tests
    // -------------------------------------------------------------------

    @Test
    public void testSelectQueryValidDatabase() {
        // Should return a JSON string with "results"
        String result = sql.SelectQuery("Fridges", "SELECT 1 AS test_column");
        assertNotNull("Result should not be null", result);
        assertTrue("Result should contain 'results'", result.contains("results"));
    }

    @Test
    public void testWriteLogAndGetAllReports() throws SQLException {
        sql.WriteLog("Test message", 1);

        ArrayList<Report> logs = sql.GetAllReportsOfType(1);
        assertNotNull("Logs list should not be null", logs);
        assertFalse("Logs list should not be empty", logs.isEmpty());
    }

    // -------------------------------------------------------------------
    // Food Items (Add / Remove) Tests
    // -------------------------------------------------------------------

    @Test
    public void testAddItemToFridge() throws SQLException {
        FoodItem.addFoodItem = new FoodItem();
        FoodItem.addFoodItem.SetFoodName("FridgeTestItem");
        FoodItem.addFoodItem.SetFoodID(9001);
        FoodItem.addFoodItem.SetExpirationDate(LocalDate.now().plusDays(5));
        FoodItem.addFoodItem.SetWeight(2.5);

        boolean result = sql.AddItemToFridge();
        assertTrue("Item should be added successfully", result);
    }

    @Test
    public void testRemoveItemFromFridge() throws SQLException {
        // First add an item
        FoodItem.addFoodItem = new FoodItem();
        FoodItem.addFoodItem.SetFoodName("RemoveThisItem");
        FoodItem.addFoodItem.SetFoodID(9002);
        FoodItem.addFoodItem.SetExpirationDate(LocalDate.now().plusDays(2));
        FoodItem.addFoodItem.SetWeight(1.0);
        sql.AddItemToFridge();

        // Now remove it
        boolean removed = sql.RemoveItemFromFridge(9002);
        assertTrue("Item should be removed successfully", removed);
        // FoodItem.cachedFoodItem should now hold the removed item
        assertNotNull("Cached food item should be set", FoodItem.cachedFoodItem);
        assertEquals("RemoveThisItem", FoodItem.cachedFoodItem.GetFoodName());
    }

    // -------------------------------------------------------------------
    // Account / User Tests
    // -------------------------------------------------------------------

    @Test
    public void testCreateUserAndAuthenticate() throws SQLException {
        //Hash the password before passing it to CreateUser
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(TEST_PASSWORD);
        // Create a user
        boolean created = sql.CreateUser("Accounts", TEST_USERNAME, 2, hashedPassword);
        assertTrue("User should be created", created);

        // Authenticate the user
        boolean auth = sql.AuthenticateUserFromDB(TEST_USERNAME, TEST_PASSWORD);
        assertTrue("User should authenticate", auth);
    }

    @Test
    public void testAuthenticateInvalidUser() throws SQLException {
        boolean auth = sql.AuthenticateUserFromDB("noUser", "noPass");
        assertFalse("Invalid user should not authenticate", auth);
    }

    @Test
    public void testUpdatePassword() throws SQLException {
        // Create a user
        sql.CreateUser("Accounts", "testUser02", 2, "oldPass");

        // Update the password
        boolean updated = sql.UpdatePassword("testUser02", "newPass");
        assertTrue("Password update should succeed", updated);

        // Check authentication with new password
        boolean authNew = sql.AuthenticateUserFromDB("testUser02", "newPass");
        assertTrue("Should authenticate with new password", authNew);
    }

    @Test
    public void testDeleteUser() throws SQLException {
        // Create a user
        sql.CreateUser("Accounts", "testUser03", 2, "testPass03");

        // Delete the user
        boolean deleted = sql.DeleteUser("testUser03");
        assertTrue("User should be deleted", deleted);
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        ArrayList<User> users = sql.GetAllUsers();
        assertNotNull("Users list should not be null", users);
    }

    @Test
    public void testUpdateRole() throws SQLException {
        // Create user
        sql.CreateUser("Accounts", "testUser04", 2, "testPass04");
        // Update role
        boolean updated = sql.UpdateRole("testUser04", 3);
        assertTrue("Role should be updated", updated);

        // Check role
        int role = sql.GetRole("testUser04");
        assertEquals("Role should be 3 now", 3, role);
    }

    @Test
    public void testGetRoleInvalidUser() throws SQLException {
        int role = sql.GetRole("noSuchUser999");
        assertEquals("Role should be 0 for non-existent user", 0, role);
    }

    // -------------------------------------------------------------------
    // Inventory / Shelves Tests
    // -------------------------------------------------------------------

    @Test
    public void testGetShelvesByFridge() throws SQLException {
        // This test needs a fridge. We can add a fridge and then read its shelves.
        // addFridge automatically creates shelves for it if successful.
        boolean fridgeAdded = sql.addFridge(2001, 300.0);
        assertTrue("Fridge should be added", fridgeAdded);

        // Find the newly created fridge. This might need the next ID approach.
        // We'll just check if there's at least one fridge and test with the last one.
        ArrayList<Fridge> fridges = sql.GetAllFridges();
        assertNotNull("Should have a list of fridges", fridges);
        assertFalse("There should be at least one fridge", fridges.isEmpty());
        Fridge latest = fridges.get(fridges.size() - 1);

        ArrayList<Shelf> shelves = sql.GetShelvesByFridge(latest.GetFridgeId());
        assertNotNull("Shelves list should not be null", shelves);
        assertFalse("Shelves should not be empty", shelves.isEmpty());
    }

    @Test
    public void testGetFoodByShelf() throws SQLException {
        // We need a shelf first. We can reuse the fridge from the previous test.
        // Just pick any known shelf ID after adding a fridge.
        // For simplicity, we take the last fridge, then the first shelf from it.
        ArrayList<Fridge> fridges = sql.GetAllFridges();
        assertNotNull(fridges);
        assertFalse(fridges.isEmpty());
        Fridge fridge = fridges.get(fridges.size() - 1);

        ArrayList<Shelf> shelves = sql.GetShelvesByFridge(fridge.GetFridgeId());
        assertNotNull(shelves);
        assertFalse(shelves.isEmpty());
        Shelf shelf = shelves.get(0);

        ArrayList<FoodItem> foods = sql.GetFoodByShelf(shelf.GetShelfId());
        assertNotNull("Foods should not be null", foods);
        // Possibly empty if we haven't inserted anything for that shelf, but it's still a valid test
    }

    // -------------------------------------------------------------------
    // Order System Tests
    // -------------------------------------------------------------------

    @Test
    public void testAddOrderAndGetOrder() throws SQLException {
        // Create an order
        Order order = new Order();
        order.SetOrderId(3001);
        order.SetFood(new ArrayList<>());
        order.SetOrderDate(LocalDate.now());
        order.SetDeliveryDate(LocalDate.now().plusDays(3));
        order.SetFridgeId(1); 

        boolean added = sql.AddOrderToDB(order);
        assertTrue("Order should be added", added);

        Order fetched = sql.GetOrderFromDB(3001);
        assertNotNull("Fetched order should not be null", fetched);
        assertEquals("Order ID should match", 3001, fetched.GetOrderId());
    }

    @Test
    public void testGetAllOrdersForHistory() throws SQLException {
        ArrayList<Order> orders = sql.GetAllOrdersForHistory();
        assertNotNull("Orders should not be null", orders);
        // Could be empty if no orders exist
    }

    @Test
    public void testGetAllAvailableFoodForOrder() throws SQLException {
        // Checks the FoodSupplier DB
        ArrayList<FoodItem> items = sql.GetAllAvailableFoodForOrder();
        assertNotNull("Items should not be null", items);
        // Could be empty if none are in the supplier table
    }

    @Test
    public void testDeleteFoodFromSupplier() throws SQLException {
        sql.DeleteFoodFromSupplier(9999);
    }

    // -------------------------------------------------------------------
    // Fridge Management Tests
    // -------------------------------------------------------------------

    @Test
    public void testAddFridge() throws SQLException {
        boolean added = sql.addFridge(5000, 100.0);
        assertTrue("Fridge should be added", added);
    }

    @Test
    public void testModifyFridge() throws SQLException {
        // We need a fridge to modify
        boolean added = sql.addFridge(5001, 200.0);
        assertTrue("Fridge created for modify test", added);

        // Now modify it
        // We don't know the fridgeID for sure, so let's get the last one
        ArrayList<Fridge> fridges = sql.GetAllFridges();
        Fridge lastFridge = fridges.get(fridges.size() - 1);

        boolean modified = sql.modifyFridge(
            lastFridge.GetFridgeId(),
            9999,           // new serial
            300.0,          // new max capacity
            10.0            // new current capacity
        );
        assertTrue("Fridge should be modified", modified);
    }

    @Test
    public void testDeleteFridge() throws SQLException {
        // Add a fridge to delete
        boolean added = sql.addFridge(6000, 50.0);
        assertTrue("Fridge created for delete test", added);

        // Get it
        ArrayList<Fridge> fridges = sql.GetAllFridges();
        Fridge lastFridge = fridges.get(fridges.size() - 1);

        // Delete the last fridge
        boolean deleted = sql.deleteFridge(lastFridge.GetFridgeId());
        assertTrue("Fridge should be deleted", deleted);
    }

    @Test
    public void testGetAllFridges() throws SQLException {
        ArrayList<Fridge> fridges = sql.GetAllFridges();
        assertNotNull("Fridges should not be null", fridges);
    }
    
    
    // -------------------------------------------------------------------
    // Alerts Tests
    // -------------------------------------------------------------------
    /*
    @Test
    public void testWriteAlert() throws SQLException {
        sql.writeAlert("Test alert message");
    }

    @Test
    public void testGetAllAlerts() throws SQLException {
        ArrayList<Alerts> alerts = sql.GetAllAlerts();
        assertNotNull("Alerts list should not be null", alerts);
    }

    @Test
    public void testCheckExpiringFood() throws SQLException {
        sql.checkExpiringFood();
        ArrayList<Alerts> alerts = sql.GetAllAlerts();
        assertNotNull("Alerts should be populated after checking expiring food", alerts);
    } */
}
