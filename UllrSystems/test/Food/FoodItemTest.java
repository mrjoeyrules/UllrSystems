package Food;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class FoodItemTest {

    @Test
    public void testGettersAndSetters() {
        FoodItem item = new FoodItem();
        item.SetFoodID(10);
        item.SetFoodName("Banana");
        item.SetExpirationDate(LocalDate.of(2025, 12, 31));
        item.SetWeight(2.5);
        item.SetShelfId(3);
        item.SetFridgeId(4);

        assertEquals("FoodID should be 10.", 10, item.GetFoodID());
        assertEquals("FoodName should be 'Banana'.", "Banana", item.GetFoodName());
        assertEquals("ExpirationDate should be 2025-12-31.", 
                     LocalDate.of(2025, 12, 31), item.GetExpirationDate());
        assertEquals("Weight should be 2.5.", 2.5, item.GetWeight(), 0.0001);
        assertEquals("ShelfId should be 3.", 3, item.GetShelfId());
        assertEquals("FridgeId should be 4.", 4, item.GetFridgeId());
    }
    
    @Test
    public void testStaticCachedFoodItem() {
        FoodItem.cachedFoodItem = null;
        assertNull("cachedFoodItem should be null initially.", FoodItem.cachedFoodItem);

        FoodItem temp = new FoodItem();
        temp.SetFoodID(99);
        FoodItem.cachedFoodItem = temp;

        assertNotNull("cachedFoodItem should not be null now.", FoodItem.cachedFoodItem);
        assertEquals("cachedFoodItem's ID should be 99.", 99, FoodItem.cachedFoodItem.GetFoodID());
    }
    
    @Test
    public void testStaticAddFoodItem() {
        FoodItem.addFoodItem = null;
        assertNull("addFoodItem should be null initially.", FoodItem.addFoodItem);

        FoodItem temp = new FoodItem();
        temp.SetFoodName("Apples");
        FoodItem.addFoodItem = temp;

        assertNotNull("addFoodItem should not be null now.", FoodItem.addFoodItem);
        assertEquals("addFoodItem's name should be 'Apples'.", 
                     "Apples", FoodItem.addFoodItem.GetFoodName());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
public void testExpiredFood() {
    FoodItem item = new FoodItem();
    item.SetExpirationDate(LocalDate.now().minusDays(1));
}

@Test(expected = IllegalArgumentException.class)
public void testNegativeWeight() {
    FoodItem item = new FoodItem();
    item.SetWeight(-5.0);
}

    
    
}

