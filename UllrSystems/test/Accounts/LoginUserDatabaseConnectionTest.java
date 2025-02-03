package Accounts;

import Databasing.SQLInterfacing;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * This test case verifies that the database connection is working by
 * directly invoking the real SQLInterfacing.AuthenticateUserFromDB method.
 * 
 * It assumes that the test database contains a user with username "testUser"
 * and password "testPass". Adjust the credentials as needed.
 */
public class LoginUserDatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() throws Exception {
        // Create a real instance of SQLInterfacing.
        SQLInterfacing sql = new SQLInterfacing();
        
        // Directly call the authentication method.
        // (Ensure that your test database is set up so that these credentials are valid.)
        boolean isAuthenticated = sql.AuthenticateUserFromDB("testUser", "testPass");
        
        // Assert that the authentication returned true.
        // This means the database connection is working and the credentials are valid.
        assertTrue("Database connection failed or credentials are invalid.", isAuthenticated);
    }
}
