package Databasing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/generate-expiry-alerts")
public class GenerateAlertsServlet extends HttpServlet {

    private SQLInterfacing dbInterface = new SQLInterfacing();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        try {
            // Actually create alerts for soon-expiring items
            dbInterface.checkExpiringFood();
            response.getWriter().println("Alerts generated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error generating alerts: " + e.getMessage());
        }
    }
}
