package Databasing;

import Food.FoodItem;
import Order.Order;
import Order.Shelf;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mrjoe
 */
public class SQLInterfacing {

    //////////////// GENERAL SQL ///////////////
    private Connection getConnection(String database) { //creates the connection to server for a specific database
        Connection conn = null;
        String username = "Joseph";
        String password = "Joseph01";
        String url = "jdbc:postgresql://81.110.173.250:5432/" + database; // url of server, postgresql is the server type, IP is the server ip with the port its on and adds on the database specified
        while (conn == null) {
            try {
                // Connect to PostgreSQL database
                conn = DriverManager.getConnection(url, username, password); // actually connects using the url above and username and password for admin acc
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

    ///////// Account System
    public boolean AuthenticateUserFromDB(String username, String rawPassword) throws SQLException {
        Connection conn = getConnection("Accounts"); // connect to db
        String query = "SELECT password, role FROM users WHERE username = ?"; // selects the stored password from users table where the username equals what user specifies
        boolean isPasswordCorrect = false;
        boolean isUserCorrectRole = false;
        try (PreparedStatement stmt = conn.prepareStatement(query)) { // prepars the query
            stmt.setString(1, username); // inputs the username into the ? in query
            ResultSet rs = stmt.executeQuery(); // runs the query

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                int role = rs.getInt("role");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                isPasswordCorrect = encoder.matches(rawPassword, storedPassword);// returns a bool if rawpassword matches the stored hashed password
                if (isPasswordCorrect && (role == 1)) {
                    conn.close();
                    isUserCorrectRole = true;
                }
            } else {
                System.out.println("User not found");
                conn.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
            return false;
        }
        conn.close();
        return isUserCorrectRole; // needed for try statement and function to work should never be called though
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

    /////////// ORDERS /////////////
    public ArrayList<Order> GetAllInProgressOrders() throws SQLException, ParseException {
        Connection conn = getConnection("Fridges");
        ArrayList<Order> orders = new ArrayList<Order>();
        String query = "SELECT * FROM orders WHERE status = ?";
        JSONParser parser = new JSONParser();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "In-Progress");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.SetOrderId(rs.getInt("orderid"));
                JSONArray foodArray = (JSONArray) parser.parse(rs.getString("food"));
                order.SetFood(foodArray);
                order.SetOrderDate(rs.getObject("orderdate", LocalDate.class));
                order.SetDeliveryDate(rs.getObject("deliverydate", LocalDate.class));
                order.SetStatus(rs.getString("status"));
                order.SetFridgeId(rs.getInt("fridgeid"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return orders;
    }

    public boolean UpdateStatusOfOrder(int orderId) throws SQLException {
        Connection conn = getConnection("Fridges");
        boolean isComplete = false;
        String query = "UPDATE orders SET status = ? WHERE orderid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "Complete");
            stmt.setInt(2, orderId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                isComplete = true;
            } else {
                conn.close();
                System.out.println("Error updating SQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return isComplete;
    }

    public Order GetOrderById(int orderId) throws SQLException, ParseException {
        Connection conn = getConnection("Fridges");
        Order order = new Order();
        JSONParser parser = new JSONParser();
        String query = "SELECT * FROM orders WHERE orderid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                order.SetOrderId(rs.getInt("orderid"));
                JSONArray foodArray = (JSONArray) parser.parse(rs.getString("food"));
                order.SetFood(foodArray);
                order.SetOrderDate(rs.getObject("orderdate", LocalDate.class));
                order.SetDeliveryDate(rs.getObject("deliverydate", LocalDate.class));
                order.SetStatus(rs.getString("status"));
                order.SetFridgeId(rs.getInt("fridgeid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return order;
    }

    public double getFridgeMaxCapacity(int fridgeId) throws SQLException {
        Connection conn = getConnection("Fridges");
        double maxCapacity = 0;
        String query = "SELECT maxcapacity FROM fridge WHERE fridgeid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxCapacity = rs.getDouble("maxcapacity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return maxCapacity;
    }

    public double getFridgeCurrentCapacity(int fridgeId) throws SQLException {
        Connection conn = getConnection("Fridges");
        double currentCapacity = 0;
        String query = "SELECT currentcapacity FROM fridge WHERE fridgeid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentCapacity = rs.getDouble("currentcapacity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return currentCapacity;
    }

    public double getShelfMaxCapacity(int shelfId) throws SQLException {
        Connection conn = getConnection("Fridges");
        double maxCapacity = 0;
        String query = "SELECT maxcapacity FROM shelves WHERE shelfid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, shelfId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxCapacity = rs.getDouble("maxcapacity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return maxCapacity;
    }

    public double getShelfCurrentCapacity(int shelfId) throws SQLException {
        Connection conn = getConnection("Fridges");
        double currentCapacity = 0;
        String query = "SELECT currentcapacity FROM shelves WHERE shelfid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, shelfId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentCapacity = rs.getDouble("currentcapacity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return currentCapacity;
    }

    public void updateFridgeCapacity(int fridgeId, double newCapacity) throws SQLException {
        Connection conn = getConnection("Fridges");
        String query = "UPDATE fridge SET currentcapacity = ? WHERE fridgeid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newCapacity);
            stmt.setInt(2, fridgeId);
            stmt.executeUpdate();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
    }
    
    public void updateShelfCapacity(int shelfId, double newCapacity) throws SQLException {
        Connection conn = getConnection("Fridges");
        String query = "UPDATE shelves SET currentcapacity = ? WHERE shelfid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newCapacity);
            stmt.setInt(2, shelfId);
            stmt.executeUpdate();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
    }
    
    public ArrayList<Shelf> GetShelvesByFridge(int fridgeId) throws SQLException{
        Connection conn = getConnection("Fridges");
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        String query = "SELECT * FROM shelves WHERE fridgeid = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Shelf shelf = new Shelf();
                shelf.SetShelfId(rs.getInt("shelfid"));
                shelf.SetFridgeId(rs.getInt("fridgeid"));
                shelf.SetShelfName(rs.getString("shelfname"));
                shelf.SetMaxCapacity(rs.getDouble("maxcapacity"));
                shelf.SetCurrentCapacity(rs.getDouble("currentcapacity"));
                shelves.add(shelf);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return shelves;
    }

    //////////////// LOGGING SYSTEM ///////////////    
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
        String query = "INSERT INTO " + table + " (eventid, eventtype, eventtext, eventtime) VALUES (?,?,?,?)";
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
        }
        conn.close();
        return isEntered;
    }

    //////////// ADDING FOOD TO FRIDGE
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
        }
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
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Failed to cache item");
        }
        conn.close();
        return isComplete;
    }

    public void insertFoodItem(FoodItem food) throws SQLException{
        Connection conn = getConnection("Fridges");
        int foodId = GetRowCount(conn, "food")  + 1;
        String query = "INSERT INTO food (foodname, itemid, expirationdate, weight, shelfid, fridgeid) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, food.GetFoodName());
            stmt.setInt(2, foodId);
            stmt.setObject(3, food.GetExpirationDate());
            stmt.setDouble(4, food.GetWeight());
            stmt.setInt(5, food.GetShelfId());
            stmt.setInt(6, food.GetFridgeId());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
    }
    
    public ArrayList<FoodItem> GetDeliveredFood(int fridgeId) throws SQLException{
        ArrayList<FoodItem> deliveredFood = new ArrayList<>();
        Connection conn = getConnection("Fridges");
        String query = "SELECT itemid, foodname, weight, expirationdate FROM food WHERE fridgeid = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, fridgeId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                FoodItem food = new FoodItem();
                food.SetFoodID(rs.getInt("itemid"));
                food.SetFoodName(rs.getString("foodname"));
                food.SetWeight(rs.getDouble("weight"));
                food.SetExpirationDate(rs.getObject("expirationdate", LocalDate.class));
                deliveredFood.add(food);
            }
        }catch(SQLException e){
            e.printStackTrace();
            conn.close();
        }
        conn.close();
        return deliveredFood;
    }
}
