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
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mrjoe
 */
@WebServlet("/updateCapacity")
public class UpdateCapacity extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject requestData = (JSONObject) parser.parse(new InputStreamReader(req.getInputStream(), "UTF-8"));
            int orderId = Integer.parseInt(requestData.get("orderId").toString());
            String foodName = (String) requestData.get("foodName");
            int shelfId = Integer.parseInt(requestData.get("shelfId").toString());
            int fridgeId = Integer.parseInt(requestData.get("fridgeId").toString());
            double foodWeight = Double.parseDouble(requestData.get("foodWeight").toString());
            
            LocalDate expirationDate;
            try{
                expirationDate = LocalDate.parse(requestData.get("expirationDate").toString());
            }catch(DateTimeParseException e){
                responseJson.put("error", "Invalid expiration date format. Use YYYY-MM-DD.");
                resp.getWriter().write(responseJson.toString());
                return;
            }
            
            double fridgeMax = sql.getFridgeMaxCapacity(fridgeId);
            double fridgeCurrent = sql.getFridgeCurrentCapacity(fridgeId);
            double shelfMax = sql.getShelfMaxCapacity(shelfId);
            double shelfCurrent = sql.getShelfCurrentCapacity(shelfId);
            
            if(fridgeCurrent + foodWeight > fridgeMax){
                responseJson.put("error", "Fridge is full! Cannot add food");
            }else if (shelfCurrent + foodWeight > shelfMax) {
                responseJson.put("error", "Shelf is full! Cannot add food.");
            } else {
                FoodItem food = new FoodItem();
                food.SetFoodName(foodName);
                food.SetFridgeId(fridgeId);
                food.SetShelfId(shelfId);
                food.SetExpirationDate(expirationDate);
                food.SetWeight(foodWeight);
                
                sql.insertFoodItem(food);
                
                sql.updateFridgeCapacity(fridgeId, fridgeCurrent + foodWeight);
                sql.updateShelfCapacity(shelfId, shelfCurrent + foodWeight);
                responseJson.put("success", "Food successfully placed on shelf.");
            }
        } catch (ParseException ex) {
            Logger.getLogger(UpdateCapacity.class.getName()).log(Level.SEVERE, null, ex);
            responseJson.put("error", "Error updating capacity");
        } catch (SQLException ex) {
            Logger.getLogger(UpdateCapacity.class.getName()).log(Level.SEVERE, null, ex);
            responseJson.put("error", "Error updating capacity");
        }catch(Exception e){
            e.printStackTrace();
            responseJson.put("error", "Error updating capacity");
        }
        resp.getWriter().write(responseJson.toString());
    }
}
