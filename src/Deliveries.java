import java.util.*;

/**
 * All the deliveries.
 */
public class Deliveries {
    private final Map<Integer, Delivery> deliveries = new TreeMap<>();

    /**
     * Add a delivery.
     * @param aDelivery The delivery to be added.
     */
    public void addDelivery(Delivery aDelivery) {
        deliveries.put(aDelivery.deliveryNumber(), aDelivery);
    }

    /**
     * Get the set of delivery numbers that have
     * not already been put in the warehouse.
     * @return The delivery numbers.
     */
    public List<Integer> getOutstandingDeliveryNumbers()
    {
        List<Integer> outstanding = new ArrayList<>();
        for(Delivery aDelivery : deliveries.values()) {
            if(! aDelivery.isInTheWarehouse()) {
                outstanding.add(aDelivery.deliveryNumber());
            }
        }
        return outstanding;
    }

    /**
     * Get a delivery.
     * @param deliveryNumber The delivery number.
     * @return The delivery with the given number.
     */
    public Delivery getDelivery(int deliveryNumber)
    {
        return deliveries.get(deliveryNumber);
    }

    /**
     * Print the deliveries.
     */
    public void printDeliveries() {
        for (Delivery aDelivery : deliveries.values()) {
            System.out.println(aDelivery);
        }
    }
}