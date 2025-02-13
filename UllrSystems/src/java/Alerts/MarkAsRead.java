package Alerts;

import Databasing.SQLInterfacing;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/markAsRead")
public class MarkAsRead extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String alertIdRaw = request.getParameter("alertId");
        if (alertIdRaw == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing alert ID.");
            return;
        }

        int alertId = Integer.parseInt(alertIdRaw);
        SQLInterfacing sql = new SQLInterfacing();
        boolean marked;
        try {
            marked = sql.MarkAsRead(alertId);
            if (marked) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarkAsRead.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
