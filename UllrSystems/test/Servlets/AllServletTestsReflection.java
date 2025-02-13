package Servlets;  // Adjust this package name if needed

import Fridge.AddFridgeServlet;
import Fridge.DeleteFridgeServlet;
import Fridge.ModifyFridgeServlet;
import Inventory.InventoryServlet;
import OrderingSystem.GetAllOrderFoods;
import OrderingSystem.GetAllOrdersServlet;
import Alerts.AlertsServlet;
import Fridge.Fridge;
import OrderingSystem.OrderFood;
import Reports.GetAllReportsServlet;
import OrderingSystem.GetAllFridgesServlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Complete test file using reflection and fake HttpServletRequest/Response.
public class AllServletTestsReflection {

    // ---------------------------------------------------------------------
    // 1) Reflection helper methods to call the protected doGet/doPost
    // ---------------------------------------------------------------------
    private void invokeDoGet(Object servlet, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Method method = servlet.getClass().getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);
    }

    private void invokeDoPost(Object servlet, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Method method = servlet.getClass().getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        method.setAccessible(true);
        method.invoke(servlet, request, response);
    }

    // ---------------------------------------------------------------------
    // 2) Minimal FakeHttpServletRequest / FakeHttpServletResponse implementations
    // ---------------------------------------------------------------------
    private static class FakeHttpServletRequest implements HttpServletRequest {
        private final Map<String, String> parameters = new HashMap<>();
        private BufferedReader reader = null;
        private String method = "GET"; // default to GET

        public void setParameter(String name, String value) {
            parameters.put(name, value);
        }

        public void setContent(String content) {
            this.reader = new BufferedReader(new StringReader(content));
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String getParameter(String name) {
            return parameters.get(name);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return reader;
        }

        @Override
        public String getMethod() {
            return method;
        }

        // All other methods throw UnsupportedOperationException.
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

        @Override
        public HttpServletMapping getHttpServletMapping() {
            return HttpServletRequest.super.getHttpServletMapping(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public PushBuilder newPushBuilder() {
            return HttpServletRequest.super.newPushBuilder(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public Map<String, String> getTrailerFields() {
            return HttpServletRequest.super.getTrailerFields(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public boolean isTrailerFieldsReady() {
            return HttpServletRequest.super.isTrailerFieldsReady(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void setCharacterEncoding(Charset encoding) {
            HttpServletRequest.super.setCharacterEncoding(encoding); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public Enumeration<String> getParameterNames() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String[] getParameterValues(String string) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getRequestId() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getProtocolRequestId() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public ServletConnection getServletConnection() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    private static class FakeHttpServletResponse implements HttpServletResponse {
        private final StringWriter stringWriter = new StringWriter();
        private final PrintWriter writer = new PrintWriter(stringWriter);

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        public String getOutput() {
            writer.flush();
            return stringWriter.toString();
        }

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
        // All remaining methods throw UnsupportedOperationException
        @Override public void addCookie(Cookie cookie) { throw new UnsupportedOperationException(); }
        @Override public boolean containsHeader(String name) { throw new UnsupportedOperationException(); }
        @Override public String encodeURL(String url) { throw new UnsupportedOperationException(); }
        @Override public String encodeRedirectURL(String url) { throw new UnsupportedOperationException(); }
        public String encodeUrl(String url) { throw new UnsupportedOperationException(); }
        public String encodeRedirectUrl(String url) { throw new UnsupportedOperationException(); }
        @Override public void sendError(int sc, String msg) { throw new UnsupportedOperationException(); }
        @Override public void sendError(int sc) { throw new UnsupportedOperationException(); }
        @Override public void sendRedirect(String location) { throw new UnsupportedOperationException(); }
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

        @Override
        public void sendRedirect(String location, boolean clearBuffer) throws IOException {
            HttpServletResponse.super.sendRedirect(location, clearBuffer); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void sendRedirect(String location, int sc) throws IOException {
            HttpServletResponse.super.sendRedirect(location, sc); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void sendRedirect(String string, int i, boolean bln) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setTrailerFields(Supplier<Map<String, String>> supplier) {
            HttpServletResponse.super.setTrailerFields(supplier); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public Supplier<Map<String, String>> getTrailerFields() {
            return HttpServletResponse.super.getTrailerFields(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void setCharacterEncoding(Charset encoding) {
            HttpServletResponse.super.setCharacterEncoding(encoding); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }
    }

    // ---------------------------------------------------------------------
    // 3) Test methods for each servlet (each test times out after 5000ms)
    // ---------------------------------------------------------------------





    @Test(timeout = 5000)
    public void testInventoryServletNoFridgeId() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");
        // No fridgeId parameter set

        InventoryServlet servlet = new InventoryServlet();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("InventoryServlet output should not be null.", result);
        // Expect JSON with "fridges"
        assertTrue("Should contain 'fridges' JSON array.", result.contains("\"fridges\""));
    }

    @Test(timeout = 5000)
    public void testInventoryServletWithFridgeId() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");
        request.setParameter("fridgeId", "1");

        InventoryServlet servlet = new InventoryServlet();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("InventoryServlet output should not be null.", result);
        // Expect JSON with "fridge" and "shelves"
        assertTrue("Should contain 'fridge' object.", result.contains("\"fridge\""));
        assertTrue("Should contain 'shelves' array.", result.contains("\"shelves\""));
    }

    @Test(timeout = 5000)
    public void testGetAllOrderFoods() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");

        GetAllOrderFoods servlet = new GetAllOrderFoods();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("GetAllOrderFoods output should not be null.", result);
        // Expect JSON with "foods"
        assertTrue("Should contain 'foods' JSON array.", result.contains("\"foods\""));
    }
    
    @Test(timeout = 5000)
    public void testAlertsServlet() throws Exception {
    FakeHttpServletRequest request = new FakeHttpServletRequest();
    FakeHttpServletResponse response = new FakeHttpServletResponse();

    request.setMethod("GET");

    AlertsServlet servlet = new AlertsServlet();
    invokeDoGet(servlet, request, response);

    String result = response.getOutput();
    System.out.println("AlertsServlet response: " + result);
    assertNotNull("AlertsServlet output should not be null.", result);

    // If it returns JSON array, you can check whether it's empty or not.
    assertTrue("Should contain [ or { in the JSON response.", result.contains("[") || result.contains("{"));
    }






    @Test(timeout = 5000)
    public void testGetAllReportsServletWithValidType() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");
        request.setParameter("eventType", "1"); // e.g., Admin logs

        GetAllReportsServlet servlet = new GetAllReportsServlet();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("GetAllReportsServlet output should not be null.", result);
        // Expect JSON with "events" array
        assertTrue("Should contain 'events' array.", result.contains("\"events\""));
    }

    @Test(timeout = 5000)
    public void testGetAllReportsServletWithInvalidType() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");
        request.setParameter("eventType", "-999");  // invalid event type

        GetAllReportsServlet servlet = new GetAllReportsServlet();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("GetAllReportsServlet output should not be null.", result);
        // Expect JSON containing "error"
        assertTrue("Output should contain 'error'.", result.contains("error"));
    }

    @Test(timeout = 5000)
    public void testGetAllFridgesServlet() throws Exception {
        FakeHttpServletRequest request = new FakeHttpServletRequest();
        FakeHttpServletResponse response = new FakeHttpServletResponse();

        request.setMethod("GET");

        GetAllFridgesServlet servlet = new GetAllFridgesServlet();
        invokeDoGet(servlet, request, response);

        String result = response.getOutput();
        assertNotNull("GetAllFridgesServlet output should not be null.", result);
        // Expect JSON with "fridges"
        assertTrue("Should contain 'fridges'.", result.contains("\"fridges\""));
    }
    
    
    @Test
    public void testMaxSerialNumberLength() {
    Fridge fridge = new Fridge();
    String maxSerial = "A".repeat(255);
    fridge.SetSerialNumber(maxSerial);
    assertEquals(255, fridge.GetSerialNumber().length());
}
    
    
    
    
    @Test(expected = DateTimeParseException.class)
    public void testInvalidDateInput() {
    LocalDate.parse("2024-02-30"); // Invalid date
}



    
    
}

