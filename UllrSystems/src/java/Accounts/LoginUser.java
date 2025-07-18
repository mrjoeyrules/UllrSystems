package Accounts;

import Databasing.SQLInterfacing;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mrjoeyrules
 */
@WebServlet("/loginUser") // runs when loginUser action is complete. Form submit button
public class LoginUser extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SQLInterfacing sql = new SQLInterfacing();
        String username = request.getParameter("username"); // gets username from form
        String rawPassword = request.getParameter("password"); // gets password from form

        try {
            boolean isLoggedIn = sql.AuthenticateUserFromDB(username, rawPassword); // runs password check from sql
            if (isLoggedIn) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username); // sets session attribute username to username to allow for unfiltered access to site
                int role = sql.GetRole(username);
                session.setAttribute("role", role);
                response.sendRedirect("inventory.html"); // if logged in correct send to next page
            } else {
                try {
                    sql.WriteLog(("User " + username + " attempted to login but password was incorrect"), 1);
                } catch (SQLException ex) {
                    Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.sendRedirect("index.html?error=Username+or+Password+is+incorrect."); // if incorrect send error to page
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
