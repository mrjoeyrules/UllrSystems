package Databasing;
import Accounts.PasswordGenerator;
import Accounts.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author mrjoe
 */
public class SQLInterfacing {
    private Connection getConnection(String database) { //creates the connection to server for a specific database
        Connection conn = null;
        String username = "Joseph";
        String password = "Joseph01";
        String url = "jdbc:postgresql://81.110.173.250:5432/" + database; // url of server, postgresql is the server type, IP is the server ip with the port its on and adds on the database specified
        while (conn == null) {
            try {
                // Connect to PostgreSQL database
                conn = DriverManager.getConnection(url, username, password); // actually connects using the url above and username and password for admin acc
                System.out.println("Connected to the PostgreSQL server successfully."); // console test to prove connection succeded
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return conn;
    }

    public boolean CreateUser(String database, String newUsername, int role) {
        boolean isCreated = false;
        Connection conn = getConnection(database); // need to change db to use ssl or ssh
        PasswordGenerator pwg = new PasswordGenerator();
        String newPassword = pwg.getNewPassword(); // makes a new password for a user
        String insertSql = "INSERT INTO users (username, role, password) VALUES (?, ?, ?)"; // insert SQL command
        try(PreparedStatement stmt = conn.prepareStatement(insertSql)){
            stmt.setString(1, newUsername); // binds the ? marks to be these values
            stmt.setInt(2, role);
            stmt.setString(3, newPassword);
            int rowsInserted = stmt.executeUpdate(); // runs the sql statement and outputs the amount of rows sent.

            if (rowsInserted > 0) {
                System.out.println("A row has been inserted successfully");
                conn.close();
                isCreated = true;
            }
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
            e.printStackTrace();
        }
        return isCreated;
    }

    public boolean AuthenticateUserFromDB(String username, String rawPassword) throws SQLException {
        Connection conn = getConnection("Accounts"); // connect to db
        String query = "SELECT password, role FROM users WHERE username = ?"; // selects the stored password from users table where the username equals what user specifies
        boolean isPasswordCorrect = false;
        try (PreparedStatement stmt = conn.prepareStatement(query)){ // prepars the query
            stmt.setString(1, username); // inputs the username into the ? in query
            ResultSet rs = stmt.executeQuery(); // runs the query

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                int role = rs.getInt("role");
                System.out.println(storedPassword);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                isPasswordCorrect = encoder.matches(rawPassword, storedPassword);// returns a bool if rawpassword matches the stored hashed password
                if(isPasswordCorrect && (role == 2 || role == 3)){
                    UserInfo(conn, username);// sets user info
                    conn.close();
                    return isPasswordCorrect; // returns that a user has entered users name and password correctly.
                }
            } else {
                System.out.println("User not found");
                return isPasswordCorrect;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return isPasswordCorrect;
        }
        return isPasswordCorrect; // needed for try statement and function to work should never be called though
    }

    private void UserInfo(Connection conn, String username){
        String query = "SELECT role FROM users WHERE username = ?"; // selects the role from user table where the username is entered username
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int role = rs.getInt("role"); // sets users role and username in user class.
                User.currentUser = new User(username, role);
            }
            else{
                System.out.println("User not found"); // should never run at this point do to previous code
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private int GetRowCount(Connection conn, String tableName ) throws SQLException{
        String query = "SELECT COUNT(*) AS totalRows FROM " + tableName;
        int rowCount = 0;
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                rowCount = rs.getInt("totalRows");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return rowCount;
    }
    private LocalDateTime GetTimestamp(){
        LocalDateTime now = LocalDateTime.now();
        return now;
    }
            
            
            
    public boolean WriteLog(String logMessage, int eventType) throws SQLException{
        Connection conn = getConnection("AdminInfo");
        boolean isEntered = false;
        String table = "";
        if(eventType == 1){
            table = "adminlogs";
        } else if(eventType == 2){
            table = "hselogs";
        }else{
            System.out.println("You didnt enter a correct event type");
            return isEntered; // stops code from running and crashing
        }
        String query = "INSERT INTO "+ table + " (eventid, eventype, eventtext, eventtime) VALUES (?,?,?,?)";
        int eventid = GetRowCount(conn, table)  + 1;
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, eventid);
            stmt.setInt(2, eventType);
            stmt.setString(3, logMessage);
            stmt.setObject(4, GetTimestamp());
            int rowsInserted = stmt.executeUpdate();
            if(rowsInserted > 0){
                System.out.println("Rows inserted");
                isEntered = true;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        conn.close();
        return isEntered;
    }


    public String SelectQuery(String database, String query) {
        Connection conn = getConnection(database); //Gets the connection from above function
        try(PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()){ // does the sql query and stores the results
            String results = ConvertResultSetToJson(rs); // converts results to a string json
            conn.close(); // closes the connection to save resources
            return results; // returns string results
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    


    private String ConvertResultSetToJson(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray(); // creates a new jsonarray
        ResultSetMetaData metadata = rs.getMetaData(); // gets meta data from the resultset
        int numColumns = metadata.getColumnCount(); // gets the number of columns in the resultset

        //iterate rows
        while (rs.next())  { // while there is a next row in result set
            JSONObject obj = new JSONObject();      //extends HashMap
            //iterate columns
            for (int i = 1; i <= numColumns; ++i) {
                String column_name = metadata.getColumnName(i); // get column name of this column
                obj.put(column_name, rs.getObject(column_name).toString()); // convert item in the column name to string from the resultset
            }
            json.add(obj); // add the object above to the json
        }
        JSONObject resultsObject = new JSONObject();
        resultsObject.put("results", json); // puts the jsonarray into a jsonobject
        return resultsObject.toJSONString(); // returns the json as a string
    }
}
