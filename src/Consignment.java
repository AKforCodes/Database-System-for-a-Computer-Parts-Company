import java.util.Objects;

/**
 * Superclass for Delivery, ClientOrder and PurchaseOrder.
 * It stores common details related to item inventories.
 */
public abstract class Consignment
{
    private final Date dateOrdered;
    // The items in the order.
    private final ItemInventory itemInventory;
    // Whether the order has been fulfilled or not.
    private boolean fulfilled;


    /**
     * Create a Consignment.
     * @param dateOrdered The date ordered.
     * @param itemInventory The items in the order.
     * @param fulfilled Whether the ordered have been fulfilled.
     */
    public Consignment(Date dateOrdered, ItemInventory itemInventory, boolean fulfilled)
    {
        this.dateOrdered = dateOrdered;
        this.itemInventory = itemInventory;
        this.fulfilled = fulfilled;
    }

    /**
     * Get the date of the order.
     * @return the date of the order.
     */
    public Date getDateOrdered()
    {
        return dateOrdered;
    }

    /**
     * Get the items in the order.
     * @return the items.
     */
    public ItemInventory getItemInventory()
    {
        return itemInventory;
    }

    /**
     * Whether the order has been fulfilled or not.
     * @return true if fulfilled, false otherwise.
     */
    public boolean isFulfilled()
    {
        return fulfilled;
    }

    /**
     * Set that the order has been fulfilled.
     */
    public void setFulfilled()
    {
        fulfilled = true;
    }

    @Override
    public String toString()
    {
        return String.format("ordered on %s for items: %s",
                dateOrdered, itemInventory);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Consignment) obj;
        return Objects.equals(this.dateOrdered, that.dateOrdered) &&
                Objects.equals(this.itemInventory, that.itemInventory) &&
                this.fulfilled == that.fulfilled;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(dateOrdered, itemInventory, fulfilled);
    }
}
