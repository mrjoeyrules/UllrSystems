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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/finishDelivery")
public class FinishDelivery extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            int orderId = Integer.parseInt(req.getParameter("orderid"));
            SQLInterfacing sql = new SQLInterfacing();
        try {
            boolean isComplete = sql.UpdateStatusOfOrder(orderId);
            if(isComplete){
                resp.sendRedirect("SelectOrder.html");
            }else{
                System.err.println("Error in sql");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FinishDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
