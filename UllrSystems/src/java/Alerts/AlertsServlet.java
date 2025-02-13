package Alerts;

import Databasing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/alerts")
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
        boolean first = true;
        for (Alerts alert : allAlerts) {
            if(!alert.isMarkedAsRead()){
                if(!first){
                    out.print(",");
                }

            out.print("{");
            out.print("\"alertId\":" + alert.getAlertId() + ",");
            out.print("\"alertMsg\":\"" + alert.getAlertMsg().replace("\"","\\\"") + "\",");
            out.print("\"timestamp\":\"" + alert.getTimestamp() + "\",");
            out.print("\"markedAsRead\":" + alert.isMarkedAsRead() + ",");
            out.print("\"alertType\":" + alert.getAlertType());
            out.print("}");
            
            first = false;
            }
        }
        out.print("]");
        out.flush();
    }
}
