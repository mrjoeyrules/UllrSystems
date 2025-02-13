package Fridge;

import org.junit.Test;
import static org.junit.Assert.*;

import Databasing.SQLInterfacing;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class FridgeTest {

    @Test
    public void testFridgeGettersAndSetters() {
        Fridge fridge = new Fridge();
        fridge.SetFridgeId(123);
        fridge.SetFridgeMaxCapacity(100.0);
        fridge.SetFridgeCurrentCapacity(55.5);
        fridge.SetSerialNumber("SN-ABC-XYZ");

        assertEquals("FridgeId should be 123.", 123, fridge.GetFridgeId());
        assertEquals("Max capacity should be 100.0.", 100.0, fridge.GetFridgeMaxCapacity(), 0.0001);
        assertEquals("Current capacity should be 55.5.", 55.5, fridge.GetFridgeCurrentCapacity(), 0.0001);
        assertEquals("Serial number should be 'SN-ABC-XYZ'.", 
                     "SN-ABC-XYZ", fridge.GetSerialNumber());
    }
    
 
    
}
