/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Order;

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
@WebServlet("/getAllDeliverableOrders")
public class GetAllDeliverableOrders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        JSONArray orderArray = new JSONArray();
        try{
            ArrayList<Order> orders = sql.GetAllInProgressOrders();
            for(Order order : orders){
                JSONObject orderJson = new JSONObject();
                orderJson.put("orderid", order.GetOrderId());
                orderJson.put("orderdate", order.GetOrderDate().toString());
                orderJson.put("deliverydate", order.GetDeliveryDate().toString());
                orderJson.put("food", order.GetFood().toString());
                orderJson.put("status", order.GetStatus());
                orderJson.put("fridgeid", order.GetFridgeId());
                orderArray.add(orderJson);
            }
            responseJson.put("orders", orderArray);
        }catch(Exception e){
            e.printStackTrace();
        }
        resp.getWriter().write(responseJson.toString());
        resp.getWriter().flush();
        
    }
}
