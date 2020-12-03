package bg.sofia.uni.fmi.mjt.shopping;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class MapShoppingCart implements ShoppingCart {

    public Map<Item, Integer> items;
    public ProductCatalog catalog;

    public MapShoppingCart(ProductCatalog catalog) {
        items = new HashMap<>();
        this.catalog = catalog;
    }

    public Collection<Item> getUniqueItems() {
        Collection<Item> i = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            i.add(entry.getKey());
        }
        return i;
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is null");
        }
        if (items.containsKey(item)) {
            items.put(item, 1 + items.get(item));
        } else {
            items.put(item, 1);
        }
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is null.");
        }
        if (!items.containsKey(item)) {
            throw new ItemNotFoundException("Item do not exist.");
        }

        int occurrences = items.get(item);
        if (--occurrences == 0) {
            items.remove(item);
        } else {
            items.put(item, occurrences - 1);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ProductInfo info = catalog.getProductInfo(entry.getKey().getId());
            total += info.price() * entry.getValue();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        List<Item> sortedItems = new ArrayList<>(items.keySet());
        Collections.sort(sortedItems, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return items.get(item2) - items.get(item1);
            }
        });
        return sortedItems;
    }

}
