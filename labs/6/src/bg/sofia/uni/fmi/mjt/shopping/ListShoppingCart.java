package bg.sofia.uni.fmi.mjt.shopping;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Comparator;
import java.util.TreeMap;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.HashMap;

public class ListShoppingCart implements ShoppingCart {

    private List<Item> items;
    private ProductCatalog catalog;

    public ListShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        Collection<Item> filteredItems = new HashSet<>(items);
        return filteredItems;
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        if (items.contains(item)) {
            items.remove(item);
        } else {
            throw new ItemNotFoundException("Item do not exist.");
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = create_map();
        Map<Item, Integer> sortedItems = new TreeMap<>(new Comparator<Item>() {
            public int compare(Item item1, Item item2) {
                return itemToQuantity.get(item2).compareTo(itemToQuantity.get(item1));
            }
        });
        sortedItems.putAll(itemToQuantity);
        return sortedItems.keySet();
    }

    private Map<Item, Integer> create_map() {
        HashMap<Item, Integer> itemToQuantity = new HashMap<Item, Integer>();
        for (Item item : items) {
            boolean condition = itemToQuantity.containsKey(item);
            itemToQuantity.put(item, condition ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}