package Inventory;

import org.junit.Test;
import static org.junit.Assert.*;

public class ShelfTest {

    @Test
    public void testShelfGettersAndSetters() {
        Shelf shelf = new Shelf();
        shelf.SetShelfId(10);
        shelf.SetFridgeId(20);
        shelf.SetShelfName("Top Shelf");

        assertEquals("ShelfId should be 10.", 10, shelf.GetShelfId());
        assertEquals("FridgeId should be 20.", 20, shelf.GetFridgeId());
        assertEquals("ShelfName should be 'Top Shelf'.", "Top Shelf", shelf.GetShelfName());
    }
}
