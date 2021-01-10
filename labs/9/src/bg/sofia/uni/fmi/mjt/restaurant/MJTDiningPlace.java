package bg.sofia.uni.fmi.mjt.restaurant;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;

public class MJTDiningPlace implements Restaurant {
    private int ordersCount;
    List<Chef> chefs;
    Queue<Order> pendingOrders;
    private final Object nullSize = new Object();

    public MJTDiningPlace(int numberOfChefs) {
        chefs = new LinkedList<>();
        pendingOrders = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.customer().hasVipCard() && o2.customer().hasVipCard()) {
                    return o2.meal().getCookingTime() - o1.meal().getCookingTime();
                }
                if (o1.customer().hasVipCard()) {
                    return -1;
                }
                if (o2.customer().hasVipCard()) {
                    return 1;
                }
                return o2.meal().getCookingTime() - o1.meal().getCookingTime();
            }
        });
        int id = 0;
        System.out.println("Open");
        for (int i = 0; i < numberOfChefs; i++) {
            chefs.add(new Chef(id, this));
            id++;
        }
        for (Chef chef : chefs) {
            chef.start();
        }
    }

    @Override
    public void submitOrder(Order order) {
        synchronized (this) {
            pendingOrders.add(order);
            ordersCount++;
            this.notifyAll();
        }
    }

    @Override
    public Order nextOrder() {
        synchronized (this) {
            if (pendingOrders.size() == 0) {
                synchronized (nullSize) {
                    nullSize.notifyAll();
                }
                return null;
            } else {
                return pendingOrders.poll();
            }
        }
    }

    @Override
    public int getOrdersCount() {
        return ordersCount;
    }

    @Override
    public Chef[] getChefs() {
        return chefs.toArray(Chef[]::new);
    }

    @Override
    public void close() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Closing...");
        synchronized (nullSize) {
            if (pendingOrders.size() > 0) {
                try {
                    nullSize.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Chef chef :
                chefs) {
            chef.interrupt();
        }
        System.out.println("Closed.");
    }
}
