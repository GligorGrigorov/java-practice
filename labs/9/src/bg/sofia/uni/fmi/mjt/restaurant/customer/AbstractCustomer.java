package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.Meal;
import bg.sofia.uni.fmi.mjt.restaurant.Order;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;

import java.util.Random;

public abstract class AbstractCustomer extends Thread {
    private Restaurant restaurant;
    private final Random random = new Random();

    public AbstractCustomer(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        if (!isInterrupted()) {
            Meal meal = Meal.chooseFromMenu();
            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Meal ordered");
            restaurant.submitOrder(new Order(meal, this));
        }
    }

    public abstract boolean hasVipCard();

}