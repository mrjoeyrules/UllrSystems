/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Order;

/**
 *
 * @author mrjoe
 */
public class Shelf {
    private int shelfId;
    private int fridgeId;
    private String shelfName;
    private double maxCapacity;
    private double currentCapacity;
    
    public void SetShelfId(int shelfId){
        this.shelfId = shelfId;
    }
    public int GetShelfId(){
        return shelfId;
    }
    
    public void SetShelfName(String shelfName){
        this.shelfName = shelfName;
    }
    public String GetShelfName(){
        return shelfName;
    }
    public void SetFridgeId(int fridgeId){
        this.fridgeId = fridgeId;
    }
    public int GetFridgeId(){
        return fridgeId;
    }
    public void SetMaxCapacity(double maxCapacity){
        this.maxCapacity = maxCapacity;
    }
    public double GetMaxCapacity(){
        return maxCapacity;
    }
    public void SetCurrentCapacity(double currentCapacity){
        this.currentCapacity = currentCapacity;
    }
    public double GetCurrentCapacity(){
        return currentCapacity;
    }
}
