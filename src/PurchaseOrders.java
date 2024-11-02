import java.util.*;

/**
 * All the purchase orders.
 */
public class PurchaseOrders {
    private final Map<Integer, PurchaseOrder> orders = new TreeMap<>();

    /**
     * Add an order.
     * @param anOrder The order to be added.
     */
    public void addOrder(PurchaseOrder anOrder) {
        orders.put(anOrder.getOrderNumber(), anOrder);
    }

    /**
     * Print the orders.
     */
    public void printOrders() {
        for (PurchaseOrder anOrder : orders.values()) {
            System.out.println(anOrder);
        }
    }
}