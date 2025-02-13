/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Accounts;

import Databasing.SQLInterfacing;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mrjoe
 */
@WebServlet("/updateRole")
public class UpdateRole extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username"); // gets username from form
        String role = request.getParameter("role"); // and role
        int newRole = Integer.valueOf(role);
        String errorMessage = "";
        SQLInterfacing sql = new SQLInterfacing();
        boolean actionComplete = false;
        try {
            actionComplete = sql.UpdateRole(username, newRole);
            if (actionComplete) {
                String message = username + "'s role has been updated";
                response.setContentType("application/json");
                 sql.WriteLog("User " + session.getAttribute("username") + " updated the role for account with username" + username + " and changed the role to " + newRole, 1);
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            }else{
                errorMessage = "Error updating role";
                sql.WriteLog("User " + session.getAttribute("username") + " attempted to updat the role for account with username" + username + " and change the role to " + newRole, 1);
                response.sendRedirect("UpdateRole.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails
            }
        } catch (SQLException ex) {
             Logger.getLogger(UpdateRole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
