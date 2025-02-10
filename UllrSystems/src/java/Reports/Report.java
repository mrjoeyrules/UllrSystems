/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reports;

import java.time.LocalDateTime;

/**
 *
 * @author mrjoe
 */
public class Report {
    private int eventId;
    private int eventType;
    private String eventText;
    private LocalDateTime eventTime;
    
    public int GetEventId(){
        return eventId;
    }
    public void SetEventId(int eventId){
        this.eventId = eventId;
    }
    public int GetEventType(){
        return eventType;
    }
    public void SetEventType(int eventType){
        this.eventType = eventType;
    }
    public String GetEventText(){
        return eventText;
    }
    public void SetEventText(String eventText){
        this.eventText = eventText;
    }
    public LocalDateTime GetEventTime(){
        return eventTime;
    }
    public void SetEventTime(LocalDateTime eventTime){
        this.eventTime = eventTime;
    }
    
}
