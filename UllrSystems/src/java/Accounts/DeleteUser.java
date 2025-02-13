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
@WebServlet("/deleteUser")
public class DeleteUser extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username"); // gets username from form
        String errorMessage = "";
        SQLInterfacing sql = new SQLInterfacing();
        boolean actionComplete = false;
        try {
            actionComplete = sql.DeleteUser(username);
            if (actionComplete) {
                String message = username + "' has been deleted";
                sql.WriteLog("User " + session.getAttribute("username") + " Deleted a account with username" + username + ".", 1);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                errorMessage = "Error updating role";
                sql.WriteLog("User " + session.getAttribute("username") + " attempted to delet a account with username" + username + " but was unsuccessful", 1);
                response.sendRedirect("DeleteUser.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeleteUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
