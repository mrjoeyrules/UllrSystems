/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Accounts;

import Databasing.SQLInterfacing;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.MultipartConfig;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mrjoe
 */
@WebServlet("/createUser")
@MultipartConfig
public class CreateUser extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Retrieve the data from the form
        SQLInterfacing sql = new SQLInterfacing();
        PasswordGenerator pwg = new PasswordGenerator();
        String errorMessage;
        HttpSession session = request.getSession();
        String username = request.getParameter("username"); // gets username from form
        String role = request.getParameter("role");
        int roleNum = Integer.valueOf(role);
        String newPassword = pwg.getNewPassword(); // makes a new password for a user
        String hashedPassword = sql.HashPasswords(newPassword);
        boolean isCreated = false;
        try {
            isCreated = sql.CreateUser("Accounts", username, roleNum, hashedPassword);
        } catch (SQLException ex) {
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (isCreated == false) { try {
            
            // if the user fails either server is down or the username is already in use
            sql.WriteLog("User " + session.getAttribute("username") + " Attempted to create new account with username" + username + " but was unsuccessful", 1);
            } catch (SQLException ex) {
                Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
            }
            errorMessage = "Username already in use";
            response.sendRedirect("CreateAccount.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails
            return;
        } else {
            try {
                sql.WriteLog("User " + session.getAttribute("username") + " Created a new account with username" + username + ".", 1);
            } catch (SQLException ex) {
                Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
            }
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": true, \"password\": \"" + newPassword + "\"}");
            return;
        }
    }
        
}


