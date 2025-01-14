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

/**
 *
 * @author mrjoe
 */
@WebServlet("/createUser")
@MultipartConfig
public class CreateUser extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    HttpSession session = request.getSession();
        // Retrieve the data from the form
        SQLInterfacing sql = new SQLInterfacing();
        PasswordGenerator pwg = new PasswordGenerator();
        String errorMessage;
        String username = request.getParameter("username"); // gets username from form
        String role = request.getParameter("role");
        int roleNum = Integer.valueOf(role);
        if(User.currentUser == null){
            User.currentUser = new User();
        }
        User.currentUser.setIsCreated(sql.CreateUser("Accounts", username, roleNum));
        if (User.currentUser.getIsCreated() == false) { // if the user fails either server is down or the username is already in use
            errorMessage = "Username already in use";
            response.sendRedirect("CreateAccount.html?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8")); // send error is fails
            return;
        } else {
            response.setContentType("application/json");
            session.setAttribute("username", User.currentUser.getUsername()); // sets session attribute username to the username to allow for the filtering to allow users to pages otherwise blocked
            response.getWriter().write("{\"success\": true, \"password\": \"" + User.newUser.getNewPassword() + "\"}");
        }
    }
        
}


