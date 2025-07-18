/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Accounts;

/**
 *
 * @author mrjoe
 */
public class User {
    public static User currentUser; // to allow for the current user to be pulled instead of wiping the data
    private String username = "";
    private Integer role = 0;

    public User() {

    }

    public User(String username, Integer role) {
        this.username = username;
        this.role = role;
    }
    
    public void setUsername(String loggedInUsername) {
        this.username = loggedInUsername;
    }
    public String getUsername() {
        return username;
    }

    public void setRole(Integer loggedInRole) {
        this.role = loggedInRole;
    }
    public Integer getRole() {
        return role;
    }
}
