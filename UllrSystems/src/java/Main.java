/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import Accounts.PasswordGenerator;
/**
 *
 * @author mrjoe
 */
public class Main { // use for testing
    public static void main(String[] args){
        PasswordGenerator pwg = new PasswordGenerator();
        String hashedpw = pwg.getNewPassword();
        System.out.println(hashedpw);
    }
}
