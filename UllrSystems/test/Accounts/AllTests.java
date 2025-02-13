package Accounts;

import Databasing.SQLInterfacing;
import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.SQLException;

/**
 * All tests in one file using JUnit 4.
 */
public class AllTests {

    // ============================================
    // PasswordGenerator Tests (Passing)
    // ============================================

    @Test
    public void testPasswordGeneratorLength() {
        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.getNewPassword();
        assertEquals("Password should be 9 characters long.", 9, password.length());
    }

    @Test
    public void testPasswordGeneratorNotNull() {
        PasswordGenerator generator = new PasswordGenerator();
        String password = generator.getNewPassword();
        assertNotNull("Generated password should not be null.", password);
    }

    // ============================================
    // User Tests (Passing)
    // ============================================

    @Test
    public void testUserProperties() {
        User user = new User("testuser", 3);

        // Test getters
        assertEquals("Username should be 'testuser'.", "testuser", user.getUsername());
        assertEquals("Role should be 3.", 3, (int) user.getRole());

        // Test setters
        user.setNewPassword("abc123");
        assertEquals("New password should be 'abc123'.", "abc123", user.getNewPassword());

        user.setIsCreated(true);
        assertTrue("isCreated should be true.", user.getIsCreated());
    }

    // ============================================
    // Simple Database Connectivity Test
    // ============================================
    //
    // This test will attempt a real DB call via SQLInterfacing.
    // If your DB is not running or not configured, it should fail.
    // A timeout is provided to avoid hanging indefinitely.

    @Test(timeout = 5000)  // Timeout in milliseconds (5 seconds)
    public void testDatabaseConnection() {
        SQLInterfacing sql = new SQLInterfacing();
        String testUsername = "someNonExistentUser";

        try {
            // If the DB is connected, GetRole(...) should not throw an error.
            // The returned role might be 0, -1, or another value based on your implementation.
            int role = sql.GetRole(testUsername);

            // Optionally, if your implementation returns a specific default value for non-existent users,
            // you can add an assertion here. For example:
            // assertEquals("Role for non-existent user should be -1.", -1, role);

        } catch (SQLException e) {
            // Fail immediately if an SQLException is thrown.
            fail("Database not connected or query failed: " + e.getMessage());
        }
    }
    
    
    @Test(expected = IllegalArgumentException.class)
public void testSetNullPassword() {
    User user = new User("user", 1);
    user.setNewPassword(null);
}

@Test
public void testSetLongUsername() {
    User user = new User("a".repeat(255), 1); // Assume max length 255
    assertEquals(255, user.getUsername().length());
}

@Test(expected = IllegalArgumentException.class)
public void testSetInvalidRole() {
    User user = new User("user", -1); // Negative role
}

    
}

