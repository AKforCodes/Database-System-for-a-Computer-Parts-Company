/**
 * A purchase order for new parts to be supplied.
 */
public class PurchaseOrder extends Order
{
    // Keep track of the next order number. This must be at least one
    // greater than any existing purchase order number.
    private static int nextOrderNumber = 0;

    /**
     * @param orderNumber   The order number.
     * @param dateOrdered   The date of the order.
     * @param itemInventory The items in the order.
     */
    public PurchaseOrder(int orderNumber, Date dateOrdered,
                         ItemInventory itemInventory,
                         boolean fulfilled)
    {
        super(orderNumber, dateOrdered, itemInventory, fulfilled);
        if(orderNumber >= nextOrderNumber) {
            nextOrderNumber = orderNumber + 1;
        }
    }

    /**
     * Get the next available order number.
     * @return The next available number.
     */
    public static int getNextOrderNumber()
    {
        return nextOrderNumber;
    }

    @Override
    public String toString()
    {
        return String.format("Purchase order: %s", super.toString());
    }


}