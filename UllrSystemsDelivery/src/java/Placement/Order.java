/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Placement;

/**
 *
 * @author mrjoe
 */
public class Order {
    public static Order currentOrder;
    private int orderId;
    private String[] food;
    private String orderDate;
    private String deliveryDate;
    
    public void SetOrderId(int orderId){
        this.orderId = orderId;
    }
    public int GetOrderId(){
        return orderId;
    }
    public void SetFood(String[] food){
        this.food = food;
    }
    public String[] GetFood(){
        return food;
    }
    public void SetOrderDate(String orderDate){
        this.orderDate = orderDate;
    }
    public String GetOrderDate(){
        return orderDate;
    }
    public void SetDeliveryDate(String deliveryDate){
        this.deliveryDate = deliveryDate;
    }
    public String GetDeliveryDate(){
        return deliveryDate;
    }
}
