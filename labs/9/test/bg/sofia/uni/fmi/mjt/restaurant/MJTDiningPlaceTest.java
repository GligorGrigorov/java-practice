package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MJTDiningPlaceTest {
    @Test
    public void restaurantClosingWithoutOrdersTest() {
        Restaurant restaurant = new MJTDiningPlace(2);
        int expectedNumberOfChefs = 2;
        assertEquals(expectedNumberOfChefs, restaurant.getChefs().length);
        Chef[] chefs = restaurant.getChefs();
        assertEquals(0, chefs[0].getTotalCookedMeals());
        assertEquals(0, chefs[1].getTotalCookedMeals());
        int expectedOrderCount = 0;
        assertEquals(expectedOrderCount, restaurant.getOrdersCount());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restaurant.close();
    }

    @Test
    public void restaurantWithRandomOrdersTest() {
        final int ORDERS_NUMBER = 1000;
        final int CHEFS_NUMBER = 20;
        Restaurant restaurant = new MJTDiningPlace(CHEFS_NUMBER);
        AbstractCustomer[] customers = new AbstractCustomer[ORDERS_NUMBER];
        for (int i = 0; i < ORDERS_NUMBER; i++) {
            if (i % 6 == 0) {
                customers[i] = new VipCustomer(restaurant);
            } else {
                customers[i] = new Customer(restaurant);
            }
        }
        for (int i = 0; i < ORDERS_NUMBER; i++) {
            customers[i].start();
        }
        int expectedNumberOfChefs = CHEFS_NUMBER;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(expectedNumberOfChefs, restaurant.getChefs().length);
        Chef[] chefs = restaurant.getChefs();
        int sum = 0;
        for (int i = 0; i < CHEFS_NUMBER; i++) {
            sum += chefs[i].getTotalCookedMeals();
        }
        int expectedOrderCount = ORDERS_NUMBER;
        assertEquals(expectedOrderCount, sum);
        assertEquals(expectedOrderCount, restaurant.getOrdersCount());
        restaurant.close();
    }
}
