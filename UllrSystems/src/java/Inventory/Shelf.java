/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Inventory;

/**
 *
 * @author mrjoe
 */
public class Shelf {
    private int shelfId;
    private int fridgeId;
    private String shelfName;
    
    public void SetShelfId(int shelfId){
        this.shelfId = shelfId;
    }
    public int GetShelfId(){
        return shelfId;
    }
    public void SetFridgeId(int fridgeId){
        this.fridgeId = fridgeId;
    }
    public int GetFridgeId(){
        return fridgeId;
    }
    public void SetShelfName(String shelfName){
        this.shelfName = shelfName;
    }
    public String GetShelfName(){
        return shelfName;
    }
}
