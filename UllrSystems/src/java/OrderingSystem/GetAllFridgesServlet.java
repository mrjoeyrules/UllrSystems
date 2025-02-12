/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package OrderingSystem;

import Databasing.SQLInterfacing;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author mrjoe
 */
@WebServlet("/getAllFridges")
public class GetAllFridgesServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException { 
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();
        
        try{
            ArrayList<Fridge> fridgeList = sql.GetAllFridges();
            JSONArray fridgeArray = new JSONArray();
             
             for (Fridge fridge : fridgeList){
                 fridgeArray.add(fridge.GetFridgeId());
             }
             
             responseJson.put("fridges", fridgeArray);
             
        }catch(SQLException e){
            System.err.println(e.getMessage());
            responseJson.put("error", "Could not retrieve users");
        }
        PrintWriter out = resp.getWriter();
        out.print(responseJson.toJSONString());
        out.flush();
        

    }
}
