/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Fridge;

/**
 *
 * @author mrjoe
 */
public class Fridge {
    private int fridgeId;
    private double fridgeMaxCapacity;
    private double fridgeCurrentCapacity;
    private String serialNumber;
    
    public void SetFridgeId(int fridgeId){
        this.fridgeId = fridgeId;
    }
    public int GetFridgeId(){
        return fridgeId;
    }
    
    public void SetFridgeMaxCapacity(double fridgeMaxCapacity){
        this.fridgeMaxCapacity = fridgeMaxCapacity;
    }
    
    public double GetFridgeMaxCapacity(){
        return fridgeMaxCapacity;
    }
    public void SetFridgeCurrentCapacity(double fridgeCurrentCapacity){
        this.fridgeCurrentCapacity = fridgeCurrentCapacity;
    }
    public double GetFridgeCurrentCapacity(){
        return fridgeCurrentCapacity;
    }
    
    public void SetSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
    }
    public String GetSerialNumber(){
        return serialNumber;
    }
}
