/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Order;

import Databasing.SQLInterfacing;
import Food.FoodItem;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.SQLException;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getDeliveredFood")
public class GetDeliveredFood extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        int fridgeId = Integer.parseInt(req.getParameter("fridgeid"));
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        JSONArray foodArray = new JSONArray();
        
        try{
            ArrayList<FoodItem> deliveredFood = sql.GetDeliveredFood(fridgeId);
            for(FoodItem food : deliveredFood){
                JSONObject foodJson = new JSONObject();
                foodJson.put("itemid", food.GetFoodID());
                foodJson.put("foodname", food.GetFoodName());
                foodJson.put("weight", food.GetWeight());
                foodJson.put("expirationdate", food.GetExpirationDate().toString());
                foodArray.add(foodJson);
            }
            responseJson.put("food", foodArray);
        }catch(SQLException e){
            e.printStackTrace();
            responseJson.put("error", "Failed to retrieve delivered food.");
        }
        resp.getWriter().write(responseJson.toString());
        resp.getWriter().flush();
    }
}
