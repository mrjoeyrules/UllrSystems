/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Reports;

import Databasing.SQLInterfacing;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getAllReports")
public class GetAllReportsServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
private int eventType;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String eventTypeRaw = req.getParameter("eventType");
        String dateParam = req.getParameter("date");
        int eventType = eventTypeRaw != null ? Integer.parseInt(eventTypeRaw) : -1; 
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        JSONArray logArray = new JSONArray();
        try{
            if(eventType > 0){
                 ArrayList<Report> reports = sql.GetAllReportsOfType(eventType);
                 
                 for(Report report : reports){
                     if(dateParam == null || report.GetEventTime().toString().startsWith(dateParam)){
                         JSONObject reportJson = new JSONObject();
                        reportJson.put("eventid", report.GetEventId());
                        reportJson.put("eventtext", report.GetEventText());
                        reportJson.put("eventtime", report.GetEventTime().toString());
                        reportJson.put("eventtype", report.GetEventType());
                        logArray.add(reportJson);
                     }
                 }
                 responseJson.put("events", logArray);
            }else{
                responseJson.put("error", "Invalid event type selected.");
            }
        }catch (Exception e){
            responseJson.put("error", "Server error: " + e.getMessage());
        }
        resp.getWriter().write(responseJson.toString());
    }
}
