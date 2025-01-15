/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import Accounts.PasswordGenerator;
import Databasing.SQLInterfacing;
import java.sql.SQLException;
/**
 *
 * @author mrjoe
 */
public class Main { // use for testing
    public static void main(String[] args) throws SQLException{
        SQLInterfacing sql = new SQLInterfacing();
        sql.WriteLog("The whole thing imploded", 1);
    }
}
