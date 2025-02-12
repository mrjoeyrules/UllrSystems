package Order;


import Food.FoodItem;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author mrjoe
 */
public class Order {
    private int orderId;
    private ArrayList<FoodItem> foodList;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String status;
    private int fridgeId;
    
    public void SetOrderId(int orderId){
        this.orderId = orderId;
    }
    public int GetOrderId(){
        return orderId;
    }
    public void SetFood(ArrayList<FoodItem> food){
        this.foodList = food;
    }
    
    public ArrayList<FoodItem> GetFood(){
        return foodList;
    }
    public void SetOrderDate(LocalDate orderDate){
        this.orderDate = orderDate;
    }
    public LocalDate GetOrderDate(){
        return orderDate;
    }
    public void SetDeliveryDate(LocalDate deliveryDate){
        this.deliveryDate = deliveryDate;
    }
    public LocalDate GetDeliveryDate(){
        return deliveryDate;
    }
    public void SetStatus(String status){
        this.status = status;
    }
    public String GetStatus(){
        return status;
    }
    public void SetFridgeId(int fridgeId){
        this.fridgeId = fridgeId;
    }
    public int GetFridgeId(){
        return fridgeId;
    }
    
    @Override
    public String toString() {
        return "{ \"orderId\": " + orderId +
                ", \"orderDate\": \"" + orderDate +
                "\", \"deliveryDate\": \"" + deliveryDate +
                "\", \"foodList\": " + foodList.toString() +
                " }";
    }
}
