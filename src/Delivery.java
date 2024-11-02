import java.util.Comparator;
import java.util.Objects;

/**
 * Details of a single delivery.
 * If the inTheWarehouse flag is set then the items have already been
 * stored in the warehouse. Otherwise, they are awaiting transfer.
 */
public class Delivery extends Consignment implements Comparable<Delivery>
{
    private final int deliveryNumber;

    /**
     * @param deliveryNumber The delivery number.
     * @param dateDelivered  The date of the delivery.
     * @param itemInventory  The items delivered.
     * @param inTheWarehouse Whether the delivery has been put in the warehouse.
     */
    public Delivery(int deliveryNumber, Date dateDelivered,
                    ItemInventory itemInventory, boolean inTheWarehouse)
    {
        super(dateDelivered, itemInventory, inTheWarehouse);
        this.deliveryNumber = deliveryNumber;
    }

    @Override
    public String toString()
    {
        return String.format("Delivery: %d %s %s",
                deliveryNumber,
                isFulfilled() ? "in the warehouse" : "to be unloaded",
                super.toString());
    }

    @Override
    public int compareTo(Delivery that)
    {
        return Objects.compare(this, that,
                Comparator.comparing(Delivery::deliveryNumber));
    }

    /**
     * Get the delivery number.
     * @return the delivery number.
     */
    public int deliveryNumber()
    {
        return deliveryNumber;
    }

    /**
     * Check whether the items in the delivery have been put
     * into the warehouse.
     * @return true if the items are in the warehouse.
     */
    public boolean isInTheWarehouse()
    {
        return isFulfilled();
    }

    /**
     * Indicate that the delivery is now in the warehouse.
     */
    public void setInTheWarehouse()
    {
        setFulfilled();
    }

}