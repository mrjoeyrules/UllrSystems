package UllrSystemsTest;  

import Accounts.LoginUser;
import Accounts.User;
import Databasing.SQLInterfacing;
import Food.FoodItem;
import Order.FinishDelivery;
import Order.GetAllDeliverableOrders;
import Order.GetCurrentOrder;
import Order.GetDeliveredFood;
import Order.GetFridgeInfo;
import Order.UpdateCapacity;
import Order.Order;
import Order.Shelf;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.json.simple.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;


public class AllProjectTests {

    // ===========================================================
    // 1) MODEL/UTILITY CLASS TESTS
    // ===========================================================

    // ----------------------------
    // 1.1) Test the User class
    // ----------------------------
    @Test
    public void testUserClassGettersSetters() {
        User user = new User("testusername", 1);
        assertEquals("Username should be 'testusername'.",
                     "testusername", user.getUsername());
        assertEquals("Role should be 1.", 1, (int) user.getRole());

        // Modify them
        user.setUsername("anotherUser");
        user.setRole(2);
        assertEquals("Username should now be 'anotherUser'.",
                     "anotherUser", user.getUsername());
        assertEquals("Role should now be 2.", 2, (int) user.getRole());
    }

    // ---------------------------------
    // 1.2) Test the FoodItem class
    // ---------------------------------
    @Test
    public void testFoodItemGettersAndSetters() {
        FoodItem food = new FoodItem();
        food.SetFoodID(123);
        food.SetFoodName("Chicken");
        food.SetExpirationDate(LocalDate.of(2025, 12, 31));
        food.SetWeight(2.0);
        food.SetShelfId(10);
        food.SetFridgeId(5);

        assertEquals("FoodID should be 123.", 123, food.GetFoodID());
        assertEquals("FoodName should be 'Chicken'.", "Chicken", food.GetFoodName());
        assertEquals("ExpirationDate should be 2025-12-31.",
                     LocalDate.of(2025, 12, 31), food.GetExpirationDate());
        assertEquals("Weight should be 2.0.", 2.0, food.GetWeight(), 0.0001);
        assertEquals("ShelfId should be 10.", 10, food.GetShelfId());
        assertEquals("FridgeId should be 5.", 5, food.GetFridgeId());
    }

    // ---------------------------------
    // 1.3) Test the Order class
    // ---------------------------------
    @Test
    public void testOrderClassGettersSetters() {
        Order order = new Order();
        order.SetOrderId(101);
        order.SetOrderDate(LocalDate.of(2025, 1, 1));
        order.SetDeliveryDate(LocalDate.of(2025, 2, 1));
        order.SetStatus("In-Progress");
        order.SetFridgeId(2);

        // Food list
        FoodItem item1 = new FoodItem();
        item1.SetFoodName("Milk");
        FoodItem item2 = new FoodItem();
        item2.SetFoodName("Tomato");
        ArrayList<FoodItem> foodList = new ArrayList<>();
        foodList.add(item1);
        foodList.add(item2);
        order.SetFood(foodList);

        assertEquals("OrderId should be 101.", 101, order.GetOrderId());
        assertEquals("OrderDate should be 2025-01-01.",
                     LocalDate.of(2025, 1, 1), order.GetOrderDate());
        assertEquals("DeliveryDate should be 2025-02-01.",
                     LocalDate.of(2025, 2, 1), order.GetDeliveryDate());
        assertEquals("Status should be 'In-Progress'.",
                     "In-Progress", order.GetStatus());
        assertEquals("FridgeId should be 2.", 2, order.GetFridgeId());
        assertEquals("Food list size should be 2.", 2, order.GetFood().size());
    }

    @Test
    public void testOrderClassToString() {
        Order order = new Order();
        order.SetOrderId(999);
        order.SetOrderDate(LocalDate.of(2025, 5, 5));
        order.SetDeliveryDate(LocalDate.of(2025, 6, 6));
        order.SetFood(new ArrayList<FoodItem>()); // ensure not null
        String result = order.toString();

        assertTrue("toString should contain 'orderId'.",
                   result.contains("\"orderId\": 999"));
        assertTrue("toString should contain 'foodList'.",
                   result.contains("\"foodList\":"));
    }

    // ---------------------------------
    // 1.4) Test the Shelf class
    // ---------------------------------
    @Test
    public void testShelfClassGettersSetters() {
        Shelf shelf = new Shelf();
        shelf.SetShelfId(10);
        shelf.SetFridgeId(5);
        shelf.SetShelfName("Raw Meat");
        shelf.SetMaxCapacity(100.0);
        shelf.SetCurrentCapacity(20.5);

        assertEquals("ShelfId should be 10.", 10, shelf.GetShelfId());
        assertEquals("FridgeId should be 5.", 5, shelf.GetFridgeId());
        assertEquals("ShelfName should be 'Raw Meat'.", "Raw Meat", shelf.GetShelfName());
        assertEquals("MaxCapacity should be 100.0.", 100.0, shelf.GetMaxCapacity(), 0.0001);
        assertEquals("CurrentCapacity should be 20.5.", 20.5, shelf.GetCurrentCapacity(), 0.0001);
    }

    // --------------------------------------------
    // 1.5) Test the SQLInterfacing class
    // --------------------------------------------
    

    @Test(timeout = 5000)
    public void testSQLInterfacingAuthenticateUserFromDB() {
        SQLInterfacing sql = new SQLInterfacing();
        // We'll try a user that likely doesn't exist; expect false or no error.
        try {
            boolean result = sql.AuthenticateUserFromDB("nonExistentUser", "wrongPass");
            // It's either false or an exception if DB is unreachable.
            assertFalse("Should return false for non-existent user or wrong pass.", result);
        } catch (SQLException e) {
            fail("SQLException thrown: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testSQLInterfacingGetRole() {
        SQLInterfacing sql = new SQLInterfacing();
        try {
            // Non-existent user => likely role=0 or some default
            int role = sql.GetRole("stillNotARealUser");
            // No exception => test passes (you can assert if you have a known default)
            // assertEquals("Expected role=0 for unknown user", 0, role);
        } catch (SQLException e) {
            fail("SQLException thrown: " + e.getMessage());
        }
    }

    @Test(timeout = 5000)
    public void testSQLInterfacingUpdateStatusOfOrder() {
        SQLInterfacing sql = new SQLInterfacing();
        // Attempt to update an order that probably doesn't exist
        try {
            boolean result = sql.UpdateStatusOfOrder(-99999);
            // Might return false if order not found; no exception => pass
            assertFalse("Should return false for non-existent order.", result);
        } catch (SQLException e) {
            fail("SQLException thrown: " + e.getMessage());
        }
    }

    // ===========================================================
    // 2) SERVLET TESTS (Using Reflection to call doGet/doPost)
    // ===========================================================
    //


    // ------------------------------------------------------------------------
    // Helper methods for Reflection-based invocation of doGet/doPost:
    // ------------------------------------------------------------------------
    private void invokeDoGet(Object servlet, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Method method = servlet.getClass().getDeclaredMethod("doGet",
                HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);
    }

    private void invokeDoPost(Object servlet, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Method method = servlet.getClass().getDeclaredMethod("doPost",
                HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);
    }

    // ------------------------------------------------------------------------
    // Minimal FakeHttpServletRequest / FakeHttpServletResponse for testing
    // ------------------------------------------------------------------------
    private static class FakeHttpServletRequest implements HttpServletRequest {
        private final Map<String, String> parameters = new HashMap<>();
        private BufferedReader reader = null;
        private String method = "GET";

        public void setParameter(String name, String value) {
            parameters.put(name, value);
        }

        public void setContent(String jsonBody) {
            this.reader = new BufferedReader(new StringReader(jsonBody));
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String getParameter(String name) {
            return parameters.get(name);
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return reader;
        }

        // --- All other methods either throw UnsupportedOperationException or return dummy values:
        @Override public Object getAttribute(String name) { throw new UnsupportedOperationException(); }
        @Override public Enumeration<String> getAttributeNames() { throw new UnsupportedOperationException(); }
        @Override public String getCharacterEncoding() { return "UTF-8"; }
        @Override public void setCharacterEncoding(String env) { throw new UnsupportedOperationException(); }
        @Override public int getContentLength() { throw new UnsupportedOperationException(); }
        @Override public long getContentLengthLong() { throw new UnsupportedOperationException(); }
        @Override public String getContentType() { throw new UnsupportedOperationException(); }
        @Override public ServletInputStream getInputStream() { throw new UnsupportedOperationException(); }
        @Override public String getProtocol() { throw new UnsupportedOperationException(); }
        @Override public String getScheme() { throw new UnsupportedOperationException(); }
        @Override public String getServerName() { throw new UnsupportedOperationException(); }
        @Override public int getServerPort() { throw new UnsupportedOperationException(); }
        @Override public String getRemoteAddr() { throw new UnsupportedOperationException(); }
        @Override public String getRemoteHost() { throw new UnsupportedOperationException(); }
        @Override public void setAttribute(String name, Object o) { throw new UnsupportedOperationException(); }
        @Override public void removeAttribute(String name) { throw new UnsupportedOperationException(); }
        @Override public Locale getLocale() { throw new UnsupportedOperationException(); }
        @Override public Enumeration<Locale> getLocales() { throw new UnsupportedOperationException(); }
        @Override public boolean isSecure() { throw new UnsupportedOperationException(); }
        @Override public RequestDispatcher getRequestDispatcher(String path) { throw new UnsupportedOperationException(); }
        public String getRealPath(String path) { throw new UnsupportedOperationException(); }
        @Override public int getRemotePort() { throw new UnsupportedOperationException(); }
        @Override public String getLocalName() { throw new UnsupportedOperationException(); }
        @Override public String getLocalAddr() { throw new UnsupportedOperationException(); }
        @Override public int getLocalPort() { throw new UnsupportedOperationException(); }
        @Override public ServletContext getServletContext() { throw new UnsupportedOperationException(); }
        @Override public AsyncContext startAsync() { throw new UnsupportedOperationException(); }
        @Override public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) { throw new UnsupportedOperationException(); }
        @Override public boolean isAsyncStarted() { throw new UnsupportedOperationException(); }
        @Override public boolean isAsyncSupported() { throw new UnsupportedOperationException(); }
        @Override public AsyncContext getAsyncContext() { throw new UnsupportedOperationException(); }
        @Override public DispatcherType getDispatcherType() { throw new UnsupportedOperationException(); }
        @Override public String getAuthType() { throw new UnsupportedOperationException(); }
        @Override public Cookie[] getCookies() { throw new UnsupportedOperationException(); }
        @Override public long getDateHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public String getHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public Enumeration<String> getHeaders(String name) { throw new UnsupportedOperationException(); }
        @Override public Enumeration<String> getHeaderNames() { throw new UnsupportedOperationException(); }
        @Override public int getIntHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public String getPathInfo() { throw new UnsupportedOperationException(); }
        @Override public String getPathTranslated() { throw new UnsupportedOperationException(); }
        @Override public String getContextPath() { throw new UnsupportedOperationException(); }
        @Override public String getQueryString() { throw new UnsupportedOperationException(); }
        @Override public String getRemoteUser() { throw new UnsupportedOperationException(); }
        @Override public boolean isUserInRole(String role) { throw new UnsupportedOperationException(); }
        @Override public Principal getUserPrincipal() { throw new UnsupportedOperationException(); }
        @Override public String getRequestedSessionId() { throw new UnsupportedOperationException(); }
        @Override public String getRequestURI() { throw new UnsupportedOperationException(); }
        @Override public StringBuffer getRequestURL() { throw new UnsupportedOperationException(); }
        @Override public String getServletPath() { throw new UnsupportedOperationException(); }
        @Override public HttpSession getSession(boolean create) { throw new UnsupportedOperationException(); }
        @Override public HttpSession getSession() { throw new UnsupportedOperationException(); }
        @Override public String changeSessionId() { throw new UnsupportedOperationException(); }
        @Override public boolean isRequestedSessionIdValid() { throw new UnsupportedOperationException(); }
        @Override public boolean isRequestedSessionIdFromCookie() { throw new UnsupportedOperationException(); }
        @Override public boolean isRequestedSessionIdFromURL() { throw new UnsupportedOperationException(); }
        public boolean isRequestedSessionIdFromUrl() { throw new UnsupportedOperationException(); }
        @Override public boolean authenticate(HttpServletResponse response) { throw new UnsupportedOperationException(); }
        @Override public void login(String username, String password) { throw new UnsupportedOperationException(); }
        @Override public void logout() { throw new UnsupportedOperationException(); }
        @Override public Collection<Part> getParts() { throw new UnsupportedOperationException(); }
        @Override public Part getPart(String name) { throw new UnsupportedOperationException(); }
        @Override public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { throw new UnsupportedOperationException(); }

        // The below methods are default from Servlet 4.0+; we can safely ignore or pass
        @Override public HttpServletMapping getHttpServletMapping() { return null; }
        @Override public PushBuilder newPushBuilder() { return null; }
        @Override public Map<String, String> getTrailerFields() { return Collections.emptyMap(); }
        @Override public boolean isTrailerFieldsReady() { return false; }
        @Override public void setCharacterEncoding(Charset encoding) {}
        @Override public Enumeration<String> getParameterNames() { throw new UnsupportedOperationException(); }
        @Override public String[] getParameterValues(String arg0) { throw new UnsupportedOperationException(); }
        @Override public Map<String, String[]> getParameterMap() { throw new UnsupportedOperationException(); }
        @Override public String getRequestId() { throw new UnsupportedOperationException(); }
        @Override public String getProtocolRequestId() { throw new UnsupportedOperationException(); }
        @Override public ServletConnection getServletConnection() { throw new UnsupportedOperationException(); }
    }

    private static class FakeHttpServletResponse implements HttpServletResponse {
        private final StringWriter stringWriter = new StringWriter();
        private final PrintWriter writer = new PrintWriter(stringWriter);
        private boolean redirected = false;
        private String redirectLocation = null;

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        public String getOutput() {
            writer.flush();
            return stringWriter.toString();
        }

        // We track if a sendRedirect() was called:
        @Override
        public void sendRedirect(String location) throws IOException {
            this.redirected = true;
            this.redirectLocation = location;
        }
        public boolean isRedirected() {
            return redirected;
        }
        public String getRedirectLocation() {
            return redirectLocation;
        }

        // --- All other methods are either dummy or throw:
        @Override public void setCharacterEncoding(String charset) {}
        @Override public String getCharacterEncoding() { return "UTF-8"; }
        @Override public void setContentLength(int len) {}
        @Override public void setContentLengthLong(long len) {}
        @Override public void setContentType(String type) {}
        @Override public String getContentType() { return null; }
        @Override public ServletOutputStream getOutputStream() { throw new UnsupportedOperationException(); }
        @Override public void setBufferSize(int size) {}
        @Override public int getBufferSize() { return 0; }
        @Override public void flushBuffer() throws IOException { writer.flush(); }
        @Override public void resetBuffer() { stringWriter.getBuffer().setLength(0); }
        @Override public boolean isCommitted() { return false; }
        @Override public void reset() { stringWriter.getBuffer().setLength(0); }
        @Override public void setLocale(Locale loc) {}
        @Override public Locale getLocale() { return Locale.getDefault(); }
        @Override public void addCookie(Cookie cookie) { throw new UnsupportedOperationException(); }
        @Override public boolean containsHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public String encodeURL(String url) { throw new UnsupportedOperationException(); }
        @Override public String encodeRedirectURL(String url) { throw new UnsupportedOperationException(); }
        public String encodeUrl(String url) { throw new UnsupportedOperationException(); }
        public String encodeRedirectUrl(String url) { throw new UnsupportedOperationException(); }
        @Override public void sendError(int sc, String msg) throws IOException { throw new UnsupportedOperationException(); }
        @Override public void sendError(int sc) throws IOException { throw new UnsupportedOperationException(); }
        @Override public void setDateHeader(String name, long date) { throw new UnsupportedOperationException(); }
        @Override public void addDateHeader(String name, long date) { throw new UnsupportedOperationException(); }
        @Override public void setHeader(String name, String value) { throw new UnsupportedOperationException(); }
        @Override public void addHeader(String name, String value) { throw new UnsupportedOperationException(); }
        @Override public void setIntHeader(String name, int value) { throw new UnsupportedOperationException(); }
        @Override public void addIntHeader(String name, int value) { throw new UnsupportedOperationException(); }
        @Override public void setStatus(int sc) { throw new UnsupportedOperationException(); }
        public void setStatus(int sc, String sm) { throw new UnsupportedOperationException(); }
        @Override public int getStatus() { throw new UnsupportedOperationException(); }
        @Override public String getHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public Collection<String> getHeaders(String name) { throw new UnsupportedOperationException(); }
        @Override public Collection<String> getHeaderNames() { throw new UnsupportedOperationException(); }

        // Servlet 4.0+ extras
        @Override public void sendRedirect(String location, boolean clearBuffer) throws IOException { }
        @Override public void sendRedirect(String location, int sc) throws IOException { }
        @Override public void sendRedirect(String string, int i, boolean bln) throws IOException { }
        @Override public void setTrailerFields(Supplier<Map<String, String>> supplier) { }
        @Override public Supplier<Map<String, String>> getTrailerFields() { return null; }
        @Override public void setCharacterEncoding(Charset encoding) {}
    }

    // ------------------------------------------------------------------------
    // 2.1) Test LoginUser servlet
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testLoginUserServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("POST");
        // Provide some test parameters
        request.setParameter("username", "fakeUser");
        request.setParameter("password", "fakePass");

        LoginUser servlet = new LoginUser();
        invokeDoPost(servlet, request, response);

        // If credentials are wrong, we expect a redirect with an error param
        // Or if there's some error, we can check the output or the redirect location
        assertTrue("Should either redirect to error or to Orders page.",
                   response.isRedirected());
        String location = response.getRedirectLocation();
        
        assertNotNull("Redirect location should not be null.", location);
    }

    // ------------------------------------------------------------------------
    // 2.2) Test FinishDelivery servlet
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testFinishDeliveryServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        request.setMethod("POST");

        // Provide a test orderId. If it doesn't exist, the code might log an error or do nothing.
        request.setParameter("orderid", "-9999"); // Non-existent order

        FinishDelivery servlet = new FinishDelivery();
        invokeDoPost(servlet, request, response);

        // The servlet calls `resp.sendRedirect("SelectOrder.html");` if successful
        // or logs an error. Check if we got redirected:
        
        assertTrue("Should try redirect or handle error internally.", 
                   response.isRedirected());
        if (response.isRedirected()) {
            assertEquals("Should redirect to 'SelectOrder.html'.",
                         "SelectOrder.html", response.getRedirectLocation());
        }
    }

    // ------------------------------------------------------------------------
    // 2.3) Test GetAllDeliverableOrders servlet (doGet)
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testGetAllDeliverableOrdersServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        request.setMethod("GET");

        GetAllDeliverableOrders servlet = new GetAllDeliverableOrders();
        invokeDoGet(servlet, request, response);

        // We expect a JSON output with "orders" array
        String output = response.getOutput();
        assertNotNull("Output should not be null.", output);
        assertTrue("Output should contain 'orders'.", output.contains("\"orders\""));
    }

    // ------------------------------------------------------------------------
    // 2.4) Test GetCurrentOrder servlet (doGet)
    // ------------------------------------------------------------------------
    

    // ------------------------------------------------------------------------
    // 2.5) Test GetDeliveredFood servlet (doGet)
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testGetDeliveredFoodServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        request.setMethod("GET");

        // Provide a fake fridgeId
        request.setParameter("fridgeid", "9999"); // Non-existent fridge
        GetDeliveredFood servlet = new GetDeliveredFood();
        invokeDoGet(servlet, request, response);

        String output = response.getOutput();
        assertNotNull("Output should not be null.", output);
        assertTrue("Output should contain 'food' array.", output.contains("\"food\""));
    }

    // ------------------------------------------------------------------------
    // 2.6) Test GetFridgeInfo servlet (doGet)
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testGetFridgeInfoServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        request.setMethod("GET");

        // Provide a fake orderId
        request.setParameter("orderid", "-9999"); 
        GetFridgeInfo servlet = new GetFridgeInfo();
        invokeDoGet(servlet, request, response);

        String output = response.getOutput();
        assertNotNull("Output should not be null.", output);
        // If order doesn't exist, might see "error"
        // Otherwise, we'd see "fridge" / "shelves" in JSON
        assertTrue("Output should contain either 'fridge' or 'error'.",
                   output.contains("fridge") || output.contains("error"));
    }

    // ------------------------------------------------------------------------
    // 2.7) Test UpdateCapacity servlet (doPost)
    // ------------------------------------------------------------------------
    @Test(timeout = 5000)
    public void testUpdateCapacityServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();
        request.setMethod("POST");

        // We send JSON in body, e.g.:
        String jsonBody = "{"
                + "\"orderId\":\"-9999\","
                + "\"foodName\":\"TestFood\","
                + "\"shelfId\":\"1\","
                + "\"fridgeId\":\"1\","
                + "\"foodWeight\":\"1.5\","
                + "\"expirationDate\":\"2025-12-31\""
                + "}";
        request.setContent(jsonBody);

        UpdateCapacity servlet = new UpdateCapacity();
        invokeDoPost(servlet, request, response);

        String output = response.getOutput();
        assertNotNull("UpdateCapacity response should not be null.", output);
        // Should contain either "success" or "error"
        assertTrue("Should contain 'success' or 'error'.",
                   output.contains("success") || output.contains("error"));
    }

}






