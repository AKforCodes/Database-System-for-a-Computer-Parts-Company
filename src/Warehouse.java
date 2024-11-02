import java.util.*;

/**
 * The warehouse.
 * It is divided into a rectangular grid for storing items.
 * Each item consists of a part number and a quantity
 * Only one type of part is stored in any location of the grid.
 *
 * TODO: Complete parts 1, 4, 5, 6, 7 and 8 in this class.
 */
public class Warehouse {
    // The maximum quantity in any location of the grid.
    private static final int MAX_AMOUNT = 500;
    // The number of rows and columns.
    private final int numRows, numCols;
    // The grid.
    // Empty locations must be stored as null values.
    private final Item[][] grid;

    /**
     * Create an empty warehouse of the given number of rows and columns.
     * @param numRows The number of rows.
     * @param numCols The number of columns.
     */
    public Warehouse(int numRows, int numCols){
        this.numRows = numRows;
        this.numCols = numCols;
        grid = new Item[this.numRows][this.numCols];
    }

    /**
     * TODO: Part 1a.
     * Get a list of all the locations currently storing parts.
     * @return a list of locations containing parts.
     */
    public List<Location> getPartLocations()
    {
        List<Location> partLocations = new ArrayList<>();
        // Iterate over each location in the warehouse grid
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Check if the location contains a part
                if (grid[row][col] != null) {
                    // Add the location to the list of part locations
                    partLocations.add(new Location(row, col));
                }
            }
        }
        // TODO: find all the locations in the warehouse that contain parts.
        // In other words, grid[aLocation.row()][aLocation.col()] is not null.
        // Store all the occupied locations in the partLocations list.

        return partLocations;
    }

    /**
     * TODO: Part 1b
     * Get the item (if any) at the given location.
     * @param theLocation The location.
     * @return The item, or null if the location is empty.
     */
    public Item getItemAt(Location theLocation)
    {
        // TODO: Return the item at the given location.
        return grid[theLocation.row()][theLocation.col()];
    }

    /**
     * TODO: Part 1c.
     * Get the total quantity of the given part.
     * @param partCode The part code.
     * @return the total quantity of the part.
     */
    public int getPartCount(int partCode)
    {
        int count = 0;
        // TODO find the locations with given part code and total their quantities.
        // Iterate over each location in the warehouse
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Check if the location contains the specified part
                Item item = grid[row][col];
                if (item != null && item.getPartCode() == partCode) {
                    // Increment the count by the quantity of the part at this location
                    count += item.getQuantity();
                }
            }
        }
        return count;
    }

    /**
     * TODO: Part 1d.
     * Get the list of locations of the given part.
     * @param partCode The part to locate.
     * @return A list of locations; may be empty.
     */
    public List<Location> findPart(int partCode)
    {
        List<Location> locationList = new ArrayList<>();
        // TODO: Store all locations with the given part code in locationList.
       for(int i = 0; i < grid.length; i++){
           for(int j = 0; j < grid[i].length; j++){
               if(grid[i][j] != null && grid[i][j].getPartCode() == partCode){
                   Location value = new Location(i, j);
                   locationList.add(value);
               }
           }
       }
        return locationList;
    }

    // NB: TODO Part 2 is in the PartsInventory class.
    // NB: TODO Part 3 is in the DatabaseHandler class.

    /**
     * TODO: Part 4.
     * Find out whether all the items in the order are fully in stock.
     * @param order The order to check.
     * @return true if all the items are in stock, false otherwise.
     */
    public boolean canBeFilled(ClientOrder order)
    {

        // Retrieve the item inventory from the client's order
        ItemInventory orderInventory = order.getItemInventory();

        // Iterate over each item in the order inventory
        for (Item item : orderInventory.getItems()) {
            // Check if the quantity required for the item exceeds the available quantity in the warehouse
            if (getPartCount(item.getPartCode()) < item.getQuantity()) {
                return false; // If not enough of the item is available, return false
            }
        }
        return true; // If all items can be filled, return true
    }

    /**
     * TODO: Part 5.
     * Create a purchase order to restock any items that are not in
     * the warehouse.
     * @param partsInventory The inventory of parts.
     * @return A purchase order, or null if none need restocking.
     */
    public PurchaseOrder createRestockOrder(PartsInventory partsInventory)
    {
        // A unique order number.
        int orderNumber = PurchaseOrder.getNextOrderNumber();
        // How many to order.
        int amountRequired = 50;
        ItemInventory purchaseInventory = new ItemInventory();
        PurchaseOrder thePurchaseOrder =
                new PurchaseOrder(orderNumber, Date.getNow(), purchaseInventory, false);

        for (Part part : partsInventory.getParts()) {
            // Check if the part is not present in the warehouse or its count is zero
            if (getPartCount(part.partCode()) == 0) {
                // Add the part to the purchase inventory with the required amount
                Item item = new Item(part.partCode(), amountRequired);
                purchaseInventory.addItem(item);
            }
        }

        if (purchaseInventory.getItems().isEmpty()) {
            return null; // If empty, return null
        } else {
            return thePurchaseOrder; // Otherwise, return the purchase order
        }
    }

    /**
     * TODO: Part 6.
     * Create a purchase order for the missing parts in the client's order.
     * @param clientOrder The client order.
     * @return The purchase order.
     */
    public PurchaseOrder createPurchaseOrder(ClientOrder clientOrder)
    {
        assert ! canBeFilled(clientOrder);
        // Need a unique order number.
        int orderNumber = PurchaseOrder.getNextOrderNumber();
        ItemInventory purchaseInventory = new ItemInventory();
        PurchaseOrder thePurchaseOrder =
                new PurchaseOrder(orderNumber, Date.getNow(), purchaseInventory, false);

        for (Item item : clientOrder.getItemInventory().getItems()){
            int requiredQuantity = item.getQuantity();
            int partCode = item.getPartCode();
            int partCount = getPartCount(partCode);

            // Check if the part count in the warehouse is less than the required quantity
            if (partCount < requiredQuantity) {
                // Calculate the additional quantity required
                int additionalQuantity = requiredQuantity - partCount;
                // Add the additional quantity to the purchase inventory
                Item additionalItem = new Item(partCode, additionalQuantity);
                purchaseInventory.addItem(additionalItem);
            }
        }
        return thePurchaseOrder;
    }

    /**
     * TODO: Part 7.
     * Use the order to create a pick list: a list of the locations
     * from which the parts must be taken to fulfil the order.
     * @param order The client's order.
     * @return The pick list.
     */
    public List<PickListItem> createAPickList(ClientOrder order)
    {
        List<PickListItem> pickListItems = new ArrayList<>();

        // Iterate over each item in the client's order
        for (Item clientItem : order.getItemInventory().getItems()) {
            int quantityLeft = clientItem.getQuantity();

            // Find locations storing the required part
            List<Location> partLocations = findPart(clientItem.getPartCode());

            // Iterate over each location containing the part
            for (Location location : partLocations) {
                Item warehouseItem = getItemAt(location);
                if (warehouseItem != null && quantityLeft > 0) {
                    // Calculate how many items to take from this location
                    int quantityToTake = Math.min(quantityLeft, warehouseItem.getQuantity());

                    // Create a pick list item for this location and quantity
                    pickListItems.add(new PickListItem(location, new Item(clientItem.getPartCode(), quantityToTake)));

                    // Reduce the quantity left
                    quantityLeft -= quantityToTake;

                    // Update the quantity in the warehouse
                    warehouseItem.reduceQuantity(quantityToTake);

                    // If the warehouse location is empty after taking items, set it to null
                    if (warehouseItem.getQuantity() == 0) {
                        grid[location.row()][location.col()] = null;
                    }
                }
            }

            // If there are still items left to fulfill in the order, find empty locations
            while (quantityLeft > 0) {
                List<Location> emptyLocations = getEmptyLocations();
                if (!emptyLocations.isEmpty()) {
                    // Take as many items as possible from the first empty location
                    Location emptyLocation = emptyLocations.get(0);
                    int quantityToTake = Math.min(quantityLeft, MAX_AMOUNT);
                    pickListItems.add(new PickListItem(emptyLocation, new Item(clientItem.getPartCode(), quantityToTake)));
                    quantityLeft -= quantityToTake;
                    // Update the warehouse grid
                    grid[emptyLocation.row()][emptyLocation.col()] = new Item(clientItem.getPartCode(), quantityToTake);
                } else {
                    // If no more empty locations are available, break the loop
                    break;
                }
            }
        }
        assert locationsOk();
        return pickListItems;
    }

    /**
     * TODO: Part 8.
     * Store the items in the delivery.
     * Where an item is already present in the warehouse, add it to the
     * existing locations up to the MAX_AMOUNT in a location.
     * Where an item is not present - or all existing locations of the
     * item are already full, store it in any empty location.
     * At the end of this method, no location may contain more than
     * MAX_AMOUNT of any item.
     * @param theDelivery The delivery to be distributed in the warehouse.
     * @return A list of where the items were stored.
     */
    public List<Location> storeDelivery(Delivery theDelivery) {
        List<Location> whereStored = new ArrayList<>();
        for (Item theItem : theDelivery.getItemInventory().getItems()) {
            int partCode = theItem.getPartCode();
            int quantityToAdd = theItem.getQuantity();

            // Find existing locations of the item
            List<Location> partLocations = findPart(partCode);

            // Check if there are existing locations
            if (!partLocations.isEmpty()) {
                for (Location location : partLocations) {
                    Item warehouseItem = getItemAt(location);
                    int spaceAvailable = MAX_AMOUNT - warehouseItem.getQuantity();

                    // Add quantity to existing locations up to MAX_AMOUNT
                    if (spaceAvailable > 0) {
                        int quantityToAddToLocation = Math.min(quantityToAdd, spaceAvailable);
                        warehouseItem.increaseQuantity(quantityToAddToLocation);
                        quantityToAdd -= quantityToAddToLocation;
                        whereStored.add(location);
                    }

                    // Break if all items are stored
                    if (quantityToAdd == 0) {
                        break;
                    }
                }
            }

            // If there are remaining items, store in empty locations
            while (quantityToAdd > 0) {
                List<Location> emptyLocations = getEmptyLocations();

                // Break if no empty locations available
                if (emptyLocations.isEmpty()) {
                    break;
                }

                Location emptyLocation = emptyLocations.get(0);
                Item newItem = new Item(partCode, Math.min(quantityToAdd, MAX_AMOUNT));
                addToWarehouse(emptyLocation, newItem);
                whereStored.add(emptyLocation);
                quantityToAdd -= newItem.getQuantity();
            }
        }

        assert locationsOk();
        return whereStored;
    }


    /**
     * Get a list of available part codes.
     * @return the list of codes.
     */
    public List<Integer> getAvailablePartCodes()
    {
        Set<Integer> partCodes = new HashSet<>();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item item = theRow[col];
                if (item != null && item.getQuantity() != 0) {
                    partCodes.add(item.getPartCode());
                }
            }
        }
        return new ArrayList<>(partCodes);
    }

    /**
     * Add the given item to the warehouse.
     * If there is already an item there then the part codes
     * must be identical.
     * @param theLocation Where to store the item.
     * @param anItem The item to be stored.
     */
    public void addToWarehouse(Location theLocation, Item anItem) {
        Item currentItem = grid[theLocation.row()][theLocation.col()];
        assert anItem.getQuantity() > 0;

        if(currentItem == null) {
            grid[theLocation.row()][theLocation.col()] = anItem;
        }
        else {
            assert currentItem.getPartCode() == anItem.getPartCode() :
                    "Attempt to store an item where a different type is stored";
            currentItem.increaseQuantity(anItem.getQuantity());
        }

        assert grid[theLocation.row()][theLocation.col()] != null;
        assert grid[theLocation.row()][theLocation.col()].getQuantity() <= MAX_AMOUNT;
    }

    /**
     * Print the occupied locations in the warehouse.
     */
    public void printOccupiedLocations() {
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item item = theRow[col];
                if(item != null) {
                    System.out.printf("%d,%d: %s%n", row, col, item);
                }
            }
        }
    }

    /**
     * Print a map of the warehouse.
     */
    public void printMap() {
        System.out.println("Warehouse contents in a 2D visual format:");

        // Print the column numbers first
        System.out.print("    |");
        for (int col = 0; col < numCols; col++) {
            System.out.printf(" %2d|", col);
        }
        System.out.println();
        for (int w = 0; w < numCols * 4 + 5; w++) {
            System.out.print("-");
        }
        System.out.println();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];

            System.out.printf("  %2d|", row);
            for (int col = 0; col < numCols; col++) {
                if(theRow[col] != null) {
                    System.out.printf("%3d", theRow[col].getQuantity());
                }
                else {
                    System.out.print("   ");
                }
                System.out.print("|");
            }
            System.out.println();
        }
    }

    /**
     * Check that no location has more than the MAX_AMOUNT of parts
     * or zero parts.
     * @return true if everything is ok, false otherwise.
     */
    public boolean locationsOk()
    {
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item anItem = theRow[col];
                if(anItem != null &&
                        anItem.getQuantity() > MAX_AMOUNT &&
                        anItem.getQuantity() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the number of parts at the given location.
     * @param theLocation the location.
     * @return The number of parts.
     */
    private int getQuantityAt(Location theLocation)
    {
        Item theItem = grid[theLocation.row()][theLocation.col()];
        if(theItem != null) {
            return theItem.getQuantity();
        }
        else {
            return 0;
        }
    }

    /**
     * Get a randomised list of all the empty locations.
     * @return a list of empty locations.
     */
    private List<Location> getEmptyLocations()
    {
        List<Location> emptyLocations = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            Item[] theRow = grid[row];
            for (int col = 0; col < numCols; col++) {
                Item anItem = theRow[col];
                if(anItem == null) {
                    emptyLocations.add(new Location(row, col));
                }
            }
        }
        Collections.shuffle(emptyLocations);
        return emptyLocations;
    }

}
