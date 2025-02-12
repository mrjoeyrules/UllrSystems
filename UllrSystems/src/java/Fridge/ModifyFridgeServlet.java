/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Fridge;


import Databasing.SQLInterfacing;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Yassine
 */
@WebServlet("/modifyFridge")
public class ModifyFridgeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject responseJson = new JSONObject();
        SQLInterfacing sql = new SQLInterfacing();

        try {
            int fridgeId = Integer.parseInt(request.getParameter("fridgeId")); // get all info from url sent from JS
            String serialNumberRaw = request.getParameter("serialNumber");
            int serialNumber = Integer.parseInt(serialNumberRaw); // convert url value to int
            double maxCapacity = Double.parseDouble(request.getParameter("maxCapacity"));
            double currentCapacity = Double.parseDouble(request.getParameter("currentCapacity"));

            boolean success = sql.modifyFridge(fridgeId, serialNumber, maxCapacity, currentCapacity); // run command in db either true for completion or false for error

            if (success) {
                responseJson.put("success", true);
                responseJson.put("message", "Fridge modified successfully!"); // display message for correct to user in frontend
            } else {
                responseJson.put("success", false);
                responseJson.put("error", "Failed to modify fridge."); // reverse for error
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            responseJson.put("success", false); // if error do this instead
            responseJson.put("error", e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(responseJson.toJSONString());
        out.flush();
    }
}
