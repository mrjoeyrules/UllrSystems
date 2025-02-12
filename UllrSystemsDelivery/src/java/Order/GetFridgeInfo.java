/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Order;

import Databasing.SQLInterfacing;
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
import org.json.simple.parser.ParseException;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getFridgeInfo")
public class GetFridgeInfo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();

        String orderIdRaw = req.getParameter("orderid");
        if (orderIdRaw == null) {
            resp.getWriter().write("{\"error\": \"Missing order ID \"}");
            return;
        }
        try {
            int orderId = Integer.parseInt(orderIdRaw);
            Order order = sql.GetOrderById(orderId);
            if (order != null) {
                JSONObject fridgeJson = new JSONObject();
                fridgeJson.put("fridgeId", order.GetFridgeId());
                fridgeJson.put("maxCapacity", sql.getFridgeMaxCapacity(order.GetFridgeId()));
                fridgeJson.put("currentCapacity", sql.getFridgeCurrentCapacity(order.GetFridgeId()));

                JSONArray shelvesArray = new JSONArray();
                ArrayList<Shelf> shelves = sql.GetShelvesByFridge(order.GetFridgeId());

                for (Shelf shelf : shelves) {
                    JSONObject shelfJson = new JSONObject();
                    shelfJson.put("shelfId", shelf.GetShelfId());
                    shelfJson.put("shelfName", shelf.GetShelfName());
                    shelfJson.put("maxCapacity", sql.getShelfMaxCapacity(shelf.GetShelfId()));
                    shelfJson.put("currentCapacity", sql.getShelfCurrentCapacity(shelf.GetShelfId()));
                    shelvesArray.add(shelfJson);
                }
                responseJson.put("fridge", fridgeJson);
                responseJson.put("shelves", shelvesArray);
            } else {
                responseJson.put("error", "Order not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("error", "Error retreiving fridge info");
        }
        resp.getWriter().write(responseJson.toString());
        resp.getWriter().flush();
    }

}
