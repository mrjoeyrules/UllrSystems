package Accounts;

import org.json.simple.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/getUserRole")
public class GetUserRole extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession(false);
        JSONObject json = new JSONObject();

        // If session doesn’t exist or role not set, we can default to -1 or 0
        if (session == null || session.getAttribute("role") == null) {
            json.put("role", -1);
        } else {
            Integer role = (Integer) session.getAttribute("role");
            json.put("role", role);
        }

        response.getWriter().write(json.toJSONString());
    }
}



