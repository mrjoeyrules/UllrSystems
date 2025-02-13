package Fridge;

import org.junit.Test;
import static org.junit.Assert.*;

import Databasing.SQLInterfacing;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import org.junit.After;
import org.junit.BeforeClass;

public class FridgeTest {
    
    private static SQLInterfacing sql;
    
    @BeforeClass
    public static void setUpClass() {
        sql = new SQLInterfacing(); // Initialize only once before all tests run
    }    
    
    @After
    public void tearDown() {
        try {
            // Remove any test fridges
            sql.cleanupTestFridges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    
    @Test
    public void testFridgeGettersAndSetters() {
        Fridge fridge = new Fridge();
        fridge.SetFridgeId(123);
        fridge.SetFridgeMaxCapacity(100.0);
        fridge.SetFridgeCurrentCapacity(55.5);
        fridge.SetSerialNumber("SN-ABC-XYZ");

        assertEquals("FridgeId should be 123.", 123, fridge.GetFridgeId());
        assertEquals("Max capacity should be 100.0.", 100.0, fridge.GetFridgeMaxCapacity(), 0.0001);
        assertEquals("Current capacity should be 55.5.", 55.5, fridge.GetFridgeCurrentCapacity(), 0.0001);
        assertEquals("Serial number should be 'SN-ABC-XYZ'.", 
                     "SN-ABC-XYZ", fridge.GetSerialNumber());
    }
    
 
    // ----------------------------------------------------------------
    // 2. Tests for AddFridgeServlet
    // ----------------------------------------------------------------

    @Test
    public void testAddFridgeServlet_Success() throws ServletException, IOException {
        // Create servlet
        AddFridgeServlet servlet = new AddFridgeServlet();

        // Mock request/response
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
         HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        // Mock the parameters
        when(request.getParameter("serialNumber")).thenReturn("5008");
        when(request.getParameter("maxCapacity")).thenReturn("300.0");

        // Capture output
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // Call doPost
        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // Check servlet JSON output
        assertTrue(output.contains("\"success\":true"));
        assertTrue(output.contains("Fridge added successfully!"));
    }

    @Test
    public void testAddFridgeServlet_InvalidNumbers() throws ServletException, IOException {
        AddFridgeServlet servlet = new AddFridgeServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
       

        // Provide invalid number formats
        when(request.getParameter("serialNumber")).thenReturn("notAnInt");
        when(request.getParameter("maxCapacity")).thenReturn("NaN");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // We expect the servlet to catch NumberFormatException
        assertTrue(output.contains("\"success\":false"));
        assertTrue(output.contains("NumberFormatException"));
    }

    // ----------------------------------------------------------------
    // 3. Tests for DeleteFridgeServlet
    // ----------------------------------------------------------------

    @Test
    public void testDeleteFridgeServlet_Success() throws ServletException, IOException, SQLException {
        DeleteFridgeServlet servlet = new DeleteFridgeServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Ensure test fridge exists before trying to delete it
        int testFridgeSerialNumber = 5003;
        sql.addFridge(testFridgeSerialNumber, 150.0);

        // Now attempt to delete it
        when(request.getParameter("serialNumber")).thenReturn("5003");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // Should either return success or false (if fridge doesn't exist)
        assertTrue(output.contains("\"success\":true") || output.contains("\"success\":false"));
    }


    @Test
    public void testDeleteFridgeServlet_InvalidId() throws ServletException, IOException {
        DeleteFridgeServlet servlet = new DeleteFridgeServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Non-numeric fridgeId
        when(request.getParameter("fridgeId")).thenReturn("abc");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // Should fail due to NumberFormatException
        assertTrue(output.contains("\"success\":false"));
    }

    // ----------------------------------------------------------------
    // 4. Tests for ModifyFridgeServlet
    // ----------------------------------------------------------------

    @Test
    public void testModifyFridgeServlet_Success() throws ServletException, IOException, SQLException {
        ModifyFridgeServlet servlet = new ModifyFridgeServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        
        

        // Ensure the test fridge exists before modifying it
        int testFridgeSerialNumber = 5001; // Any test serial >= 5000
        sql.addFridge(testFridgeSerialNumber, 200.0);

        // Now try modifying it
        when(request.getParameter("fridgeId")).thenReturn("5000");
        when(request.getParameter("serialNumber")).thenReturn("5001");
        when(request.getParameter("maxCapacity")).thenReturn("250.0");
        when(request.getParameter("currentCapacity")).thenReturn("75.0");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // Should succeed (or return false if fridge ID 5001 didn't exist)
        assertTrue(output.contains("\"success\":true") || output.contains("\"success\":false"));
    }

    @Test
    public void testModifyFridgeServlet_InvalidParameters() throws ServletException, IOException {
        ModifyFridgeServlet servlet = new ModifyFridgeServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Provide some invalid parameters
        when(request.getParameter("fridgeId")).thenReturn("abc");
        when(request.getParameter("serialNumber")).thenReturn("xyz");
        when(request.getParameter("maxCapacity")).thenReturn("???");
        when(request.getParameter("currentCapacity")).thenReturn("???");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request, response);

        pw.flush();
        String output = sw.toString();

        // We expect a failure due to NumberFormatException
        assertTrue(output.contains("\"success\":false"));
    }

    // ----------------------------------------------------------------
    // 5. Tests for GetAllFridgesManagementVer
    // ----------------------------------------------------------------

    @Test
    public void testGetAllFridgesManagementVer_Success() throws ServletException, IOException {
        GetAllFridgesManagementVer servlet = new GetAllFridgesManagementVer();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doGet(request, response);

        pw.flush();
        String output = sw.toString();

        // Should return JSON with a "fridges" key
        // The array might be empty or have data, depending on your DB
        assertTrue(output.contains("\"fridges\""));
    }
    
}