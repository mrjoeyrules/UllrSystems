/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Inventory;

import Databasing.SQLInterfacing;
import Food.FoodItem;
import Fridge.Fridge;
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
@WebServlet("/inventoryServlet")
public class InventoryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        PrintWriter out = resp.getWriter();

        String fridgeIdRaw = req.getParameter("fridgeId");
        JSONObject resultJson = new JSONObject();

        if (fridgeIdRaw == null) {
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
            return;
        } else {
            int fridgeId = Integer.parseInt(fridgeIdRaw);

            try {
                Fridge fridge = sql.GetFridgeById(fridgeId);
                JSONObject fridgeJson = new JSONObject();
                fridgeJson.put("fridgeId", fridge.GetFridgeId());
                fridgeJson.put("serialNumber", fridge.GetSerialNumber());
                fridgeJson.put("maxCapacity", fridge.GetFridgeMaxCapacity());
                fridgeJson.put("currentCapacity", fridge.GetFridgeCurrentCapacity());
                JSONArray shelvesArray = new JSONArray();
                ArrayList<Shelf> shelves = sql.GetShelvesByFridge(fridgeId);
                for(Shelf shelf : shelves){
                    JSONObject shelfJson = new JSONObject();
                    shelfJson.put("shelfId", shelf.GetShelfId());
                    shelfJson.put("shelfName", shelf.GetShelfName());
                    
                    JSONArray foodArray = new JSONArray();
                    ArrayList<FoodItem> foodItems = sql.GetFoodByShelf(shelf.GetShelfId());
                    for(FoodItem food : foodItems){
                        JSONObject foodJson = new JSONObject();
                        foodJson.put("foodName", food.GetFoodName());
                        foodJson.put("expirationDate", food.GetExpirationDate().toString());
                        foodJson.put("weight", food.GetWeight());
                        foodArray.add(foodJson);
                    }
                    shelfJson.put("foodItems", foodArray);
                    shelvesArray.add(shelfJson);
                }
                resultJson.put("fridge", fridgeJson);
                resultJson.put("shelves", shelvesArray);
            } catch (SQLException ex) {
                Logger.getLogger(InventoryServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.print(resultJson.toString());
        }
    }
}
