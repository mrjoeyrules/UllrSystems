package Accounts;

import Databasing.SQLInterfacing;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/getAllUsers")
public class GetAllUsersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        SQLInterfacing sql = new SQLInterfacing();
        JSONObject responseJson = new JSONObject();

        try {
            ArrayList<Accounts.User> usersList = sql.GetAllUsers();
            JSONArray usersArray = new JSONArray();

            for (Accounts.User u : usersList) {
                usersArray.add(u.getUsername());
            }

            // Return something like { "users": [ "user1", "user2", ... ] }
            responseJson.put("users", usersArray);

        } catch (SQLException e) {
            e.printStackTrace();
            responseJson.put("error", "Could not retrieve users");
        }

        PrintWriter out = resp.getWriter();
        out.print(responseJson.toJSONString());
        out.flush();
    }
}
