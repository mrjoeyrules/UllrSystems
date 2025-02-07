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
@WebServlet("/addFridge")
public class AddFridgeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject responseJson = new JSONObject();
        SQLInterfacing sql = new SQLInterfacing();

        try {
            String serialNumber = request.getParameter("serialNumber");
            double fridgeCapacity = Double.parseDouble(request.getParameter("fridgeCapacity"));

            boolean success = sql.addFridge(serialNumber, fridgeCapacity);

            if (success) {
                responseJson.put("success", true);
                responseJson.put("message", "Fridge added successfully!");
            } else {
                responseJson.put("success", false);
                responseJson.put("error", "Failed to add fridge.");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("error", e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(responseJson.toJSONString());
        out.flush();
    }
}
