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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mrjoe
 */
@WebServlet("/updatePassword")
public class UpdatePassword extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username"); // gets username from form
        String password = request.getParameter("password"); // and password and reconfirmed password
        String repeatPassword = request.getParameter("repeatPassword");
        String errorMessage = "";
        SQLInterfacing sql = new SQLInterfacing();
        boolean actionComplete = false;
        if (password.equals(repeatPassword)) {
            try {
                actionComplete = sql.UpdatePassword(username, password);
                if (actionComplete) {
                    String message = username + "'s password has been updated";
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
                } else {
                    errorMessage = "Error updating password";
                    response.sendRedirect("UpdatePassword.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails
                }
            } catch (SQLException ex) {
                Logger.getLogger(UpdatePassword.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            errorMessage = "Password do not match";
            response.sendRedirect("UpdatePassword.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails 
        }
    }
}
