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
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getAllOrderFoods")
public class GetAllOrderFoods extends HttpServlet {
@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        JSONArray foodArray = new JSONArray();
        
        try{
            ArrayList<FoodItem> foodList = sql.GetAllAvailableFoodForOrder();
            System.out.println(foodList);
             
             
             for (FoodItem food : foodList){
                 JSONObject foodJson = new JSONObject();
                 foodJson.put("foodName",food.GetFoodName());
                 foodJson.put("foodId",food.GetFoodID());
                 foodJson.put("expirationDate", (food.GetExpirationDate() != null) ? food.GetExpirationDate().toString() : "N/A");
                 foodJson.put("weight", food.GetWeight());
                 
                 foodArray.add(foodJson);
             }
             
             responseJson.put("foods", foodArray);
             
        }catch(SQLException e){
            System.err.println(e.getMessage());
            responseJson.put("error", "Could not retrieve users");
        }
        PrintWriter out = resp.getWriter();
        out.print(responseJson.toJSONString());
        out.flush();
    }
}
