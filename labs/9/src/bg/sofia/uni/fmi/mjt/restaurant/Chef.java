package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {

    private final int id;
    private final Restaurant restaurant;
    private int totalCookedMeals;

    public Chef(int id, Restaurant restaurant) {
        totalCookedMeals = 0;
        this.id = id;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        System.out.println(id + " started");
        while (true) {
            Order order = restaurant.nextOrder();
            if (order == null) {
                synchronized (restaurant) {
                    try {
                        restaurant.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } else {
                try {
                    Thread.sleep(order.meal().getCookingTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(id + " cooked " + order.meal().toString() + " to " + order.customer().hasVipCard());
                totalCookedMeals++;
            }
        }
        System.out.println(id + " finished cooking " + this.getTotalCookedMeals());
    }

    /**
     * Returns the total number of meals that this chef has cooked.
     **/
    public int getTotalCookedMeals() {
        return totalCookedMeals;
    }

}