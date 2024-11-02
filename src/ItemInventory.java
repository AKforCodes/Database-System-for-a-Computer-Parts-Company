import java.util.*;

/**
 * A collection of items.
 * These are used to store the details of client orders, purchase orders
 * and deliveries.
 */
public class ItemInventory
{
    private final Map<Integer, Item> items = new TreeMap<>();

    /**
     * Get the items in the inventory.
     * @return The items.
     */
    public Collection<Item> getItems()
    {
        return items.values();
    }

    /**
     * Add an item.
     * @param anItem The item to be added.
     */
    public void addItem(Item anItem) {
        items.put(anItem.getPartCode(), anItem);
    }

    /**
     * Print the items.
     */
    public void printItems() {
        for (Item anItem : items.values()) {
            System.out.println(anItem);
        }
    }

    @Override
    public String toString(){
        return items.values().toString();
    }
}

