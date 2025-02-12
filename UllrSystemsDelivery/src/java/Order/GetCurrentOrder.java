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
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getCurrentOrder")
public class GetCurrentOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Order order = null;
        String orderIdRaw = req.getParameter("orderid");
        int orderId = Integer.parseInt(orderIdRaw);
        JSONObject responseJson = new JSONObject();
        SQLInterfacing sql = new SQLInterfacing();
        try {
            order = sql.GetOrderById(orderId);
            JSONObject orderJson = new JSONObject();
            orderJson.put("orderid", order.GetOrderId());
            orderJson.put("orderdate", order.GetOrderDate().toString());
            orderJson.put("deliverydate", order.GetDeliveryDate().toString());
            orderJson.put("food", order.GetFood().toString());
            orderJson.put("status", order.GetStatus());
            orderJson.put("fridgeid", order.GetFridgeId());
            responseJson.put("order", orderJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(responseJson.toString());
        resp.getWriter().flush();
    }
}
