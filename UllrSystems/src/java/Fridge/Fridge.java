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
    private double fridgeCapacity;
    private String serialNumber;
    
    public void SetFridgeId(int fridgeId){
        this.fridgeId = fridgeId;
    }
    public int GetFridgeId(){
        return fridgeId;
    }
    
    public void SetFridgeCapacity(double fridgeCapacity){
        this.fridgeCapacity = fridgeCapacity;
    }
    
    public double GetFridgeCapacity(){
        return fridgeCapacity;
    }
    public void SetSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
    }
    public String GetSerialNumber(){
        return serialNumber;
    }
}
