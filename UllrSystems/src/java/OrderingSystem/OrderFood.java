/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package OrderingSystem;

import Databasing.SQLInterfacing;
import Food.FoodItem;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.time.LocalDate;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mrjoe
 */
@WebServlet("/orderFood")
public class OrderFood extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        Order order = new Order();
        try {
            BufferedReader reader = request.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            System.out.println("Raw request body: " + requestBody.toString());

            JSONParser parser = new JSONParser();
            JSONObject orderData = (JSONObject) parser.parse(requestBody.toString());
            String orderDate = (String) orderData.get("orderDate");
            String deliveryDate = (String) orderData.get("deliveryDate");
            int fridgeId = Integer.parseInt(orderData.get("fridgeId").toString());
            order.SetOrderDate(LocalDate.parse(orderDate));
            order.SetDeliveryDate(LocalDate.parse(deliveryDate));

            JSONArray foodArray = (JSONArray) orderData.get("food");
            ArrayList<FoodItem> foodList = new ArrayList<>();
            for (Object obj : foodArray) {
                JSONObject foodJson = (JSONObject) parser.parse(obj.toString());
                int foodId = Integer.parseInt(foodJson.get("foodId").toString());
                String foodName = (String) foodJson.get("foodName");
                String expirationDate = (String) foodJson.get("expirationDate");
                double weight = Double.parseDouble(foodJson.get("weight").toString());

                FoodItem foodItem = new FoodItem();
                foodItem.SetFoodID(foodId);
                foodItem.SetFoodName(foodName);
                foodItem.SetExpirationDate(LocalDate.parse(expirationDate));
                foodItem.SetWeight(weight);
                foodList.add(foodItem);
            }

            order.SetFood(foodList);
            order.SetFridgeId(fridgeId);

            boolean success = sql.AddOrderToDB(order);
            if (success) {
                for (FoodItem food : foodList) {
                    sql.DeleteFoodFromSupplier(food.GetFoodID());
                }
                responseJson.put("success", true);
                sql.WriteLog("User " + session.getAttribute("username") + " ordered food for fridgeid:  " + fridgeId + "." , 1);
            } else {
                responseJson.put("success", false);
                sql.WriteLog("User " + session.getAttribute("username") + " attempted to order food for fridgeid:  " + fridgeId + " but was unsuccessful." , 1);
                responseJson.put("error", "Database insert failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(responseJson.toJSONString());
            out.flush();
        }

    }
}
