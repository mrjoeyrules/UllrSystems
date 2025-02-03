/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OrderingSystem;

import java.time.LocalDate;

/**
 *
 * @author mrjoe
 */
public class Order {
    private int orderId;
    private String[] food;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    
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
}
