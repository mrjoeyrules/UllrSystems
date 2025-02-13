package Databasing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/alerts")
public class AlertsServlet extends HttpServlet {

    private final SQLInterfacing dbInterface = new SQLInterfacing();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      

        ArrayList<Alerts> allAlerts = new ArrayList<>();
        try {
            allAlerts = dbInterface.GetAllAlerts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        out.print("[");
        for (int i = 0; i < allAlerts.size(); i++) {
            Alerts alert = allAlerts.get(i);

            out.print("{");
            out.print("\"alertId\":" + alert.getAlertId() + ",");
            out.print("\"alertMsg\":\"" + alert.getAlertMsg().replace("\"","\\\"") + "\",");
            out.print("\"timestamp\":\"" + alert.getTimestamp() + "\",");
            out.print("\"markedAsRead\":" + alert.isMarkedAsRead() + ",");
            out.print("\"alertType\":" + alert.getAlertType());
            out.print("}");

            if (i < allAlerts.size() - 1) {
                out.print(",");
            }
        }
        out.print("]");
        out.flush();
    }
}
