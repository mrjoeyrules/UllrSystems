/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package Filters;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author mrjoe
 */
@WebFilter("*/")
public class AuthenticationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain) 
            throws IOException, ServletException { // this filter makes it so that users can not access pages that need the user to be logged in without logging in
        HttpServletRequest httpRequest = (HttpServletRequest) request; 
        HttpServletResponse httpResponse = (HttpServletResponse) response; // casts generice response and request to their http variant 
        
        HttpSession session = httpRequest.getSession(false); // get the current session do not create a new one
        String path = httpRequest.getRequestURI(); // get the path from the requests URI
        
        if(path.equals("index.html") || path.equals(httpRequest.getContextPath() + "/UllrSystems/")){ // ignore these pages as user doesnt need to be logged in to access
            chain.doFilter(request, response);
            return;
        }
        boolean loggedIn = (session != null && session.getAttribute("username") != null);
        if(loggedIn){
            chain.doFilter(request, response); // if user is logged in then allow access
        }
        else{
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.html"); // if user is not logged in redirect to login page.
        }
    }
    @Override
    public void destroy(){
        
    }
}
