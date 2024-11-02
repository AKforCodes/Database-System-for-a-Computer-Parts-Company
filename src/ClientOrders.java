import java.util.*;

/**
 * All the client orders.
 */
public class ClientOrders
{
    private final Map<Integer, ClientOrder> orders = new TreeMap<>();

    /**
     * Get the client order numbers.
     * @return The orders.
     */
    public List<Integer> getOrderNumbers()
    {
        return new ArrayList<>(orders.keySet());
    }

    /**
     * Get the given client order.
     * @param orderNumber The order number.
     * @return The order, or null if it does not exist.
     */
    public ClientOrder getOrder(int orderNumber)
    {
        return orders.get(orderNumber);
    }

    /**
     * Add an order.
     * @param anOrder The order to be added.
     */
    public void addOrder(ClientOrder anOrder) {
        orders.put(anOrder.getOrderNumber(), anOrder);
    }

    /**
     * Print the orders.
     */
    public void printOrders() {
        for (ClientOrder anOrder : orders.values()) {
            System.out.println(anOrder);
        }
    }
}
