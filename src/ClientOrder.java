import java.util.Objects;

/**
 * A client order.
 */
public class ClientOrder extends Order
{
    private final int customerCode;

    /**
     * @param orderNumber   The order number.
     * @param customerCode  The customer code.
     * @param dateOrdered   The date of the order.
     * @param itemInventory The items of the order.
     */
    public ClientOrder(int orderNumber, int customerCode,
                       Date dateOrdered, ItemInventory itemInventory,
                       boolean fulfilled)
    {
        super(orderNumber, dateOrdered, itemInventory, fulfilled);
        this.customerCode = customerCode;
    }

    @Override
    public String toString()
    {
        return String.format("For customer %d, order %s",
                            customerCode, super.toString());
    }

    /**
     * Get the customer code.
     * @return the customer code.
     */
    public int getCustomerCode()
    {
        return customerCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ClientOrder that) {
            return super.equals(obj) &&
                    this.customerCode == that.customerCode;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(customerCode, getDateOrdered(), getItemInventory(), isFulfilled());
    }

}
