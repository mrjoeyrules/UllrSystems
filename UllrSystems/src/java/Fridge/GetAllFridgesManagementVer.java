/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Fridge;

import Databasing.SQLInterfacing;
import Inventory.InventoryServlet;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/GetAllFridgesManagementVer")
public class GetAllFridgesManagementVer extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        PrintWriter out = resp.getWriter();
        JSONObject resultJson = new JSONObject();
        JSONArray fridgesArray = new JSONArray();
        try {
            ArrayList<Fridge> fridges = sql.GetAllFridges();
            for (Fridge fridge : fridges) {
                JSONObject fridgeJson = new JSONObject();
                fridgeJson.put("fridgeId", fridge.GetFridgeId());
                fridgeJson.put("serialNumber", fridge.GetSerialNumber());
                fridgeJson.put("maxCapacity", fridge.GetFridgeMaxCapacity());
                fridgeJson.put("currentCapacity", fridge.GetFridgeCurrentCapacity());
                fridgesArray.add(fridgeJson);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InventoryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        resultJson.put("fridges", fridgesArray);
        out.print(resultJson.toString());
        out.flush();
    }
}
