/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Food;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author mrjoe
 */
public class FoodItem {
    public static FoodItem cachedFoodItem;
    public static FoodItem addFoodItem;
    private int foodId;
    private String foodName;
    private LocalDate expirationDate;
    private double weight;
    
    public void SetFoodID(int foodId){
        this.foodId = foodId;
    }
    public int GetFoodID(){
        return foodId;
    }
    public void SetFoodName(String foodName){
        this.foodName = foodName;
    }
    public String GetFoodName(){
        return foodName;
    }
    public void SetExpirationDate(LocalDate expirationDate){
        this.expirationDate = expirationDate;
    }
    public LocalDate GetExpirationDate(){
        return expirationDate;
    }
    public void SetWeight(double weight){
        this.weight = weight;
    }
    public double GetWeight(){
        return weight;
    }
}
