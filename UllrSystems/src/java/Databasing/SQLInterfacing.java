package Databasing;

import Accounts.User;
import Food.FoodItem;
import Fridge.Fridge;
import Inventory.Shelf;
import OrderingSystem.Order;
import Reports.Report;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author mrjoe
 */
public class SQLInterfacing {

    /////// GENERAL SQL FUNCTIONS
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

    private int GetRowCount(Connection conn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) AS totalRows FROM " + tableName;
        int rowCount = 0;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rowCount = rs.getInt("totalRows");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowCount;
    }

    private LocalDateTime GetTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return now;
    }

    public String SelectQuery(String database, String query) {
        Connection conn = getConnection(database); //Gets the connection from above function
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) { // does the sql query and stores the results
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
        while (rs.next()) { // while there is a next row in result set
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

    //////// LOGGING SYSTEM
    public boolean WriteLog(String logMessage, int eventType) throws SQLException {
        Connection conn = getConnection("AdminInfo");
        boolean isEntered = false;
        String table = "";
        if (eventType == 1) {
            table = "adminlogs";
        } else if (eventType == 2) {
            table = "hselogs";
        } else {
            System.out.println("You didnt enter a correct event type");
            return isEntered; // stops code from running and crashing
        }
        String query = "INSERT INTO " + table + " (eventid, eventype, eventtext, eventtime) VALUES (?,?,?,?)";
        int eventid = GetRowCount(conn, table) + 1;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventid);
            stmt.setInt(2, eventType);
            stmt.setString(3, logMessage);
            stmt.setObject(4, GetTimestamp());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rows inserted");
                isEntered = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return isEntered;
    }
    
    public ArrayList<Report> GetAllReportsOfType(int eventType) throws SQLException {
        String table = "";
        ArrayList<Report> reports = new ArrayList<Report>();
        if(eventType == 1){
            table = "adminlogs";
        }else if(eventType == 2){
            table = "hselogs";
        }else{
            System.out.println("Somehome you got here, should be impossible");
            return null;
        }
        String query = "SELECT * FROM " + table; 
        Connection conn = getConnection("AdminInfo");
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Report report = new Report();
                report.SetEventId(rs.getInt("eventid"));
                report.SetEventText(rs.getString("eventtext"));
                report.SetEventType(rs.getInt("eventtype"));
                report.SetEventTime(rs.getObject("eventtime", LocalDateTime.class));
                reports.add(report);
            } 
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return reports;
    }
    

    /////// ADDING AND REMOVING FOOD ITEMS
    private boolean CacheDeletedFoodItem(int itemId, Connection conn) throws SQLException {
        boolean isComplete = false;
        String query = "SELECT * FROM food WHERE itemid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                FoodItem.cachedFoodItem = new FoodItem();
                FoodItem.cachedFoodItem.SetFoodName(rs.getString("foodname"));
                FoodItem.cachedFoodItem.SetFoodID(rs.getInt("foodid"));

                FoodItem.cachedFoodItem.SetExpirationDate(rs.getObject("expirationdate", LocalDate.class));
                FoodItem.cachedFoodItem.SetWeight(rs.getDouble("weight"));
                isComplete = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();

        }
        conn.close();
        return isComplete;
    }

    public boolean RemoveItemFromFridge(int itemId) throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Fridges");
        String query = "DELETE FROM food WHERE itemid = ?";
        if (CacheDeletedFoodItem(itemId, conn)) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Successfully deleted+ " + rowsDeleted);
                    isComplete = true;
                } else {
                    System.out.println("Error deleting");
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                conn.close();
            }
        } else {
            System.out.println("Failed to cache item");
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    public boolean AddItemToFridge() throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Fridges");
        String query = "INSERT INTO food (foodname, itemid, expirationdate, weight) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, FoodItem.addFoodItem.GetFoodName());
            stmt.setInt(2, FoodItem.addFoodItem.GetFoodID());
            stmt.setObject(3, FoodItem.addFoodItem.GetExpirationDate());
            stmt.setDouble(4, FoodItem.addFoodItem.GetWeight());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                isComplete = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    ////////// FRIDGE MANAGEMENT
    public Fridge GetFridgeById(int fridgeId) throws SQLException {
        Connection conn = getConnection("Fridges");
        String query = "SELECT * FROM fridge WHERE fridgeid = ?";
        Fridge fridge = new Fridge();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fridge.SetFridgeId(fridgeId);
                fridge.SetSerialNumber(rs.getString("serialnumber"));
                fridge.SetFridgeMaxCapacity(rs.getDouble("maxcapacity"));
                fridge.SetFridgeCurrentCapacity(rs.getDouble("currentcapacity"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return fridge;
    }

    ////// ACCOUNT SYSTEM FUNCTIONS
    public String HashPasswords(String password) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // object of password encoder
            return encoder.encode(password); // encodes the new password
        } catch (Exception e) {
            throw new RuntimeException("Password encoding failed: " + e.getMessage(), e);
        }
    }

    public boolean CreateUser(String database, String newUsername, int role, String newPassword) throws SQLException {
        boolean isCreated = false;
        Connection conn = getConnection(database);
        String insertSql = "INSERT INTO users (username, role, password) VALUES (?, ?, ?)"; // insert SQL command
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
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
            conn.close();
        }
        return isCreated;
    }

    public boolean AuthenticateUserFromDB(String username, String rawPassword) throws SQLException {
        Connection conn = getConnection("Accounts"); // connect to db
        String query = "SELECT password, role FROM users WHERE username = ?"; // selects the stored password from users table where the username equals what user specifies
        boolean isPasswordCorrect = false;
        try (PreparedStatement stmt = conn.prepareStatement(query)) { // prepars the query
            stmt.setString(1, username); // inputs the username into the ? in query
            ResultSet rs = stmt.executeQuery(); // runs the query

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                int role = rs.getInt("role");
                System.out.println(storedPassword);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                isPasswordCorrect = encoder.matches(rawPassword, storedPassword);// returns a bool if rawpassword matches the stored hashed password
                if (isPasswordCorrect && (role == 2 || role == 3)) {
                    conn.close();
                    return isPasswordCorrect; // returns that a user has entered users name and password correctly.
                }
            } else {
                System.out.println("User not found");
                return isPasswordCorrect;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
            return isPasswordCorrect;
        }
        conn.close();
        return isPasswordCorrect; // needed for try statement and function to work should never be called though
    }

    public int GetRole(String username) throws SQLException {
        Connection conn = getConnection("Accounts");
        int role = 0;
        String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getInt("role");
            } else {
                System.out.println("User not found"); // should never run at this point do to previous code
            }
        } catch (SQLException e) {
            conn.close();
            throw new RuntimeException(e);
        }
        conn.close();
        return role;
    }

    public boolean DeleteUser(String username) throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Accounts");
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                isComplete = true;
            } else {
                System.err.println("Error deleting user");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    public boolean UpdatePassword(String username, String newPassword) throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Accounts");
        String hashedPassword = HashPasswords(newPassword);
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                isComplete = true;
            } else {
                System.err.println("User doesnt exist");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    public ArrayList<User> GetAllUsers() throws SQLException {
        Connection conn = getConnection("Accounts");
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return users;
    }

    public boolean UpdateRole(String username, int newRole) throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Accounts");
        String query = "UPDATE users SET role = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newRole);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                isComplete = true;
            } else {
                System.err.println("User doesnt exist");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return isComplete;
    }
    //////////////// INVENTORY SYSTEM
    
    public ArrayList<Shelf> GetShelvesByFridge(int fridgeId) throws SQLException{
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        String query = "SELECT * FROM shelves WHERE fridgeid = ?";
        Connection conn = getConnection("Fridges");
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Shelf shelf = new Shelf();
                shelf.SetShelfId(rs.getInt("shelfid"));
                shelf.SetFridgeId(rs.getInt("fridgeid"));
                shelf.SetShelfName(rs.getString("shelfname"));
                shelves.add(shelf);
            }
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return shelves;
    }
    
    public ArrayList<FoodItem> GetFoodByShelf(int shelfId) throws SQLException{
        ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
        Connection conn = getConnection("Fridges");
        String query = "SELECT * FROM food WHERE shelfid = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, shelfId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                FoodItem food = new FoodItem();
                food.SetFoodID(rs.getInt("itemid"));
                food.SetFoodName(rs.getString("foodname"));
                food.SetExpirationDate(rs.getObject("expirationdate", LocalDate.class));
                food.SetWeight(rs.getDouble("weight"));
                food.SetShelfId(rs.getInt("shelfid"));
                food.SetFridgeId(rs.getInt("fridgeid"));
                foodItems.add(food);
            }
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return foodItems;
    }
    
    
    

    ////// ORDER SYSTEM FUNCTIONS
    public ArrayList<Fridge> GetAllFridges() throws SQLException { // also used in inventory
        Connection conn = getConnection("Fridges");
        ArrayList<Fridge> fridges = new ArrayList<>();
        String query = "SELECT * FROM fridge";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Fridge fridge = new Fridge();
                fridge.SetFridgeId(rs.getInt("fridgeid"));
                fridge.SetSerialNumber(rs.getString("serialnumber"));
                fridge.SetFridgeMaxCapacity(rs.getDouble("maxcapacity"));
                fridge.SetFridgeCurrentCapacity(rs.getDouble("currentcapacity"));
                fridges.add(fridge);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();

        return fridges;
    }

    public boolean AddOrderToDB(Order order) throws SQLException {
        boolean isComplete = false;
        Connection conn = getConnection("Fridges");
        String query = "INSERT INTO orders (orderid, food, orderdate, deliverydate) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            JSONArray foodArray = new JSONArray();
            for (FoodItem food : order.GetFood()) {
                JSONObject foodJson = new JSONObject();
                foodJson.put("foodName", food.GetFoodName());
                foodJson.put("expirationDate", food.GetExpirationDate().toString());
                foodJson.put("weight", food.GetWeight());

                foodArray.add(foodJson);
            }
            
            String foodJsonString = foodArray.toJSONString();
            int rowCount = GetRowCount(conn, "orders");
            stmt.setInt(1, rowCount + 1);
            stmt.setObject(2, foodJsonString, Types.OTHER);
            stmt.setObject(3, order.GetOrderDate());
            stmt.setObject(4, order.GetDeliveryDate());
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                isComplete = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    public Order GetOrderFromDB(int orderId) throws SQLException {
        Order order = new Order();
        Connection conn = getConnection("Fridges");
        String query = "SELECT * FROM orders WHERE orderid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                order.SetOrderId(rs.getInt("orderid"));
                order.SetFood(rs.getObject("food", ArrayList.class));
                order.SetOrderDate(rs.getObject("orderdate", LocalDate.class));
                order.SetDeliveryDate(rs.getObject("deliverydate", LocalDate.class));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return order;
    }

    public ArrayList<FoodItem> GetAllAvailableFoodForOrder() throws SQLException {
        Connection conn = getConnection("FoodSupplier");
        ArrayList<FoodItem> availableFood = new ArrayList();
        String query = "SELECT * FROM AvailableFoods";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FoodItem food = new FoodItem();
                food.SetFoodID(rs.getInt("foodid"));
                food.SetFoodName(rs.getString("foodname"));
                food.SetWeight(rs.getDouble("weight"));
                food.SetExpirationDate(rs.getObject("expirydate", LocalDate.class));
                availableFood.add(food);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return availableFood;
    }

    public ArrayList<Order> GetAllOrdersForHistory() throws SQLException {
        Connection conn = getConnection("Fridge");
        ArrayList<Order> orders = new ArrayList();
        String query = "SELECT * FROM orders";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.SetOrderId(rs.getInt("orderid"));
                order.SetFood(rs.getObject("food", ArrayList.class));
                order.SetOrderDate(rs.getObject("orderdate", LocalDate.class));
                order.SetDeliveryDate(rs.getObject("deliverydate", LocalDate.class));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
        return orders;
    }
    
    public void DeleteFoodFromSupplier(int supplierId) throws SQLException{
        Connection conn = getConnection("FoodSupplier");
        String query = "DELETE FROM availablefoods WHERE foodid = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, supplierId);
            int rowsDeleted = stmt.executeUpdate();
            if(rowsDeleted < 1){
                System.err.println("Error deleting food");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
        }
        conn.close();
    }
    public boolean addFridge(int serialNumber, double maxCapacity) throws SQLException {
        Connection conn = getConnection("Fridges");
         double currentCapacity = 0;
        String query = "INSERT INTO fridge (fridgeid, serialnumber, maxcapacity, currentcapacity) VALUES (?,?,?,?)"; // sql query to add a row into fridge table
        int rowCount = GetRowCount(conn, "fridge"); // get fridge id dynamically
        int fridgeId = rowCount + 1; // row count+1 is the fridgeid
        try (PreparedStatement stmt = conn.prepareStatement(query)) { // prepare statement
            stmt.setInt(1, fridgeId); // set questionmarks in query
            stmt.setInt(2, serialNumber);
            stmt.setDouble(3, maxCapacity);
            stmt.setDouble(4, currentCapacity);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
            return false;
        } finally {
            conn.close(); // always close conn for resources
        }
    }
    
    public boolean modifyFridge(int fridgeId, int serialNumber, double fridgeMaxCapacity, double currentCapacity) throws SQLException {
        Connection conn = getConnection("Fridges");
        String query = "UPDATE fridge SET serialnumber = ?, maxcapacity = ?, currentcapacity = ? WHERE fridgeId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, serialNumber);
            stmt.setDouble(2, fridgeMaxCapacity);
            stmt.setDouble(3, currentCapacity);
            stmt.setInt(4, fridgeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
            return false;
        } finally {
            conn.close();
        }
    }
    
    public boolean deleteFridge(int fridgeId) throws SQLException {
        Connection conn = getConnection("Fridges");
        String query = "DELETE FROM fridge WHERE fridgeId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fridgeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            conn.close();
            return false;
        } finally {
            conn.close();
        }
    }
}
