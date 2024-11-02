/**
 * A part and how many of them.
 * This class is used in lots of places.
 */
public class Item implements Comparable<Item>
{
    // The part code.
    private final int partCode;
    // The number of this part.
    private int quantity;

    /**
     * A part and a number of items.
     * @param partCode The part code.
     * @param quantity The number of items of this part code.
     */
    public Item(int partCode, int quantity)
    {
        this.partCode = partCode;
        this.quantity = quantity;
    }

    /**
     * Add the given amount to the quantity.
     * @param amount The amount to add.
     */
    public void increaseQuantity(int amount)
    {
        this.quantity += amount;
    }

    /**
     * Remove the given amount from the quantity of this item.
     * @param amount The amount to remove.
     */
    public void reduceQuantity(int amount)
    {
        this.quantity -= amount;
        assert this.quantity >= 0 : "Negative amount of " + partCode;
    }

    /**
     * Get the part code.
     * @return The part code.
     */
    public int getPartCode()
    {
        return partCode;
    }

    /**
     * Get the quantity.
     * @return The quantity.
     */
    public int getQuantity()
    {
        return quantity;
    }

    @Override
    public String toString()
    {
        return String.format("<Part code: %d, quantity %d>",
                partCode, quantity);
    }

    @Override
    public int compareTo(Item other)
    {
        return partCode - other.partCode;
    }
}
