import java.util.Comparator;
import java.util.Objects;

/**
 * A superclass for ClientOrder and PurchaseOrder.
 * It associates an order number with other details of the orders,
 * such as the item inventory and date of the order, inherited
 * from Consignment.
 */
public abstract class Order extends Consignment implements Comparable<Order>
{
    private final int orderNumber;

    /**
     * Create an Order.
     * @param orderNumber The order number.
     * @param dateOrdered The date ordered.
     * @param itemInventory The items in the order.
     * @param fulfilled Whether the ordered have been fulfilled.
     */
    public Order(int orderNumber, Date dateOrdered, ItemInventory itemInventory, boolean fulfilled)
    {
        super(dateOrdered, itemInventory, fulfilled);
        this.orderNumber = orderNumber;

    }

    /**
     * Get the order number.
     * @return the order number.
     */
    public int getOrderNumber()
    {
        return orderNumber;
    }

    @Override
    public String toString()
    {
        return String.format("%d %s", orderNumber, super.toString());
    }

    @Override
    public int compareTo(Order that)
    {
        return Objects.compare(this, that,
                Comparator.comparing(Order::getOrderNumber));
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof Order that) {
            return orderNumber == that.orderNumber && super.equals(that);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), orderNumber);
    }
}
