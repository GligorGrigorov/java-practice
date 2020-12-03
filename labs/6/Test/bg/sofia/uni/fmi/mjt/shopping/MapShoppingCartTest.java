package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapShoppingCartTest {

    private ShoppingCart mapCart;
    private ProductCatalog productCatalog;
    private Apple a1;
    private Apple a2;
    private Chocolate c1;
    private static final String ID_1 = "id1";
    private static final String ID_2 = "id2";
    private static final String ID_3 = "id3";

    @Before
    public void setup() {
        productCatalog = mock(ProductCatalog.class);
        mapCart = new MapShoppingCart(productCatalog);
        a1 = new Apple(ID_1);
        a2 = new Apple(ID_2);
        c1 = new Chocolate(ID_3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addItemThrowingExceptionTest() {
        mapCart.addItem(null);
    }

    @Test
    public void addItemTest() {
        mapCart.addItem(a1);
        Collection<Item> items = mapCart.getUniqueItems();
        assertTrue(items.contains(a1));
        assertFalse(items.contains(a2));
        assertEquals(1, items.size());
    }

    @Test
    public void getUniqueItemsTest() {
        mapCart.addItem(a1);
        mapCart.addItem(a2);
        mapCart.addItem(c1);
        mapCart.addItem(a1);
        Collection<Item> items = mapCart.getUniqueItems();
        assertTrue(items.contains(a1));
        assertTrue(items.contains(a2));
        assertTrue(items.contains(c1));
        assertEquals(3, items.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeItemThrowsIllegalArgumentExceptionTest() {
        mapCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void removeItemThrowsItemNotFoundExceptionTest() {
        mapCart.addItem(a1);
        mapCart.removeItem(c1);
    }

    @Test
    public void removeItemTest() {
        mapCart.addItem(a1);
        mapCart.addItem(c1);
        mapCart.removeItem(a1);
        int expectedSize = 1;
        Collection items = mapCart.getUniqueItems();
        assertEquals(expectedSize, items.size());
        assertTrue(items.contains(c1));
        assertFalse(items.contains(a1));
    }

    @Test
    public void removeItemWhenMoreThanOneItemTest() {
        mapCart.addItem(a1);
        mapCart.addItem(c1);
        mapCart.addItem(a1);
        mapCart.removeItem(a1);
        int expectedSize = 2;
        Collection items = mapCart.getUniqueItems();
        assertEquals(expectedSize, items.size());
        assertTrue(items.contains(c1));
        assertTrue(items.contains(a1));
    }

    @Test
    public void getSortedItemsTest() {
        mapCart.addItem(a1);
        mapCart.addItem(a2);
        mapCart.addItem(c1);
        mapCart.addItem(a1);
        mapCart.addItem(c1);
        mapCart.addItem(c1);
        Collection<Item> items = mapCart.getSortedItems();
        assertEquals(3, items.size());
        assertEquals(ID_3, getIdByIndex(0, items));
        assertEquals(ID_1, getIdByIndex(1, items));
        assertEquals(ID_2, getIdByIndex(2, items));
    }

    @Test
    public void getTotalTest() {
        double a1Price = 35.3;
        double a2Price = 21.8;
        double c1Price = 76.1;
        String description = "Test description";
        String name = "Test name";
        ProductInfo a1Info = new ProductInfo(name, description, a1Price);
        ProductInfo a2Info = new ProductInfo(name, description, a2Price);
        ProductInfo c1Info = new ProductInfo(name, description, c1Price);
        when(productCatalog.getProductInfo(a1.getId())).thenReturn(a1Info);
        when(productCatalog.getProductInfo(a2.getId())).thenReturn(a2Info);
        when(productCatalog.getProductInfo(c1.getId())).thenReturn(c1Info);
        mapCart.addItem(a1);
        mapCart.addItem(a2);
        mapCart.addItem(c1);
        mapCart.addItem(a1);
        double total = mapCart.getTotal();
        assertEquals(a1Price * 2 + a2Price + c1Price, total, 0.0001);
    }

    String getIdByIndex(int index, Collection<Item> items) {
        int i = 0;
        for (Item item :
                items) {
            if (i == index) {
                return item.getId();
            }
            i++;
        }
        return null;
    }
}
