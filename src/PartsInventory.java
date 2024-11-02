import java.util.*;

/**
 * The parts offered.
 * TODO: Complete the getCost method in this class for part 3.
 */
public class PartsInventory
{
    // The parts, indexed by part code.
    private final Map<Integer, Part> parts = new TreeMap<>();


    /**
     * TODO Part 3.
     * Calculate the cost of the given order.
     * @param order The order.
     * @return The cost of the order.
     */
    public double getCost(ClientOrder order)
    {
        double cost = 0;

        // Get the item inventory from the client's order
        ItemInventory itemInventory = order.getItemInventory();

        // Iterate through each item in the item inventory
        for (Item item : itemInventory.getItems()) {
            int partCode = item.getPartCode();
            int quantity = item.getQuantity();

            // Find the corresponding Part object using the part code
            Part part = parts.get(partCode);
            if (part != null) {
                // Multiply the price of the part by the quantity required
                double itemCost = part.price() * quantity;
                // Add the item cost to the total cost
                cost += itemCost;
            } else {
                // Handle case where part is not found in the inventory
                System.err.println("Part with code " + partCode + " not found in inventory.");
            }
        }
        return cost;
    }
    
    /**
     * Get the parts in the inventory.
     * @return The parts.
     */
    public Collection<Part> getParts()
    {
        return parts.values();
    }

    /**
     * Add a part to the inventory.
     * @param aPart The part to be added.
     */
    public void addPart(Part aPart)
    {
        parts.put(aPart.partCode(), aPart);
    }

    /**
     * Print the parts.
     */
    public void printParts() {
        for (Part aPart : parts.values()) {
            System.out.println(aPart);
        }
    }
}

