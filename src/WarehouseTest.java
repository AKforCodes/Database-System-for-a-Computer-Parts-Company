import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest
{
    // Warehouse details.
    private Warehouse theWarehouse;
    // Work with a smaller warehouse as there is no particular need for
    // a large one.
    private final int numRows = 5, numColumns = 6;
    // Part details.
    // A part code to find.
    private int partCodeToFind;
    // Locations for partCodeToFind.
    private List<Location> partLocationsToFind;
    // Quantities for partCodeToFind.
    private List<Integer> partQuantitiesToFind;
    // A location with an Item we won't be searching for.
    private final Location locationToNotFind = new Location(0, 0);
    // The maximum amount in any location.
    private static final int MAX_AMOUNT = 500;
    // The restock quantity
    private static final int RESTOCK_QUANTITY = 50;

    // Item details of those parts to be found.
    private List<Item> itemsToFind;
    // Client details.
    private final int customerCode = 1;
    // Client order date.
    private final Date orderDate = Date.getNow();
    // Support generation of random part numbers, etc.
    private static final Random rand = new Random();


    /**
     * Set up the fixture before each test.
     */
    @org.junit.jupiter.api.BeforeEach
    void setUp()
    {
        theWarehouse = new Warehouse(numRows, numColumns);

        partCodeToFind = rand.nextInt(100);
        int partCodeToNotFind = partCodeToFind * 7;

        // Where partCodeToFind will be located.
        partLocationsToFind = List.of(
                new Location(1, 1),
                new Location(2, 4),
                new Location(3, 5),
                new Location(numRows - 1, numColumns - 1));
        // Quantities of partCodeToFind in various locations.
        partQuantitiesToFind = List.of(1, 2, 3, 4);

        // Create the Items that will be stored in the warehouse.
        itemsToFind = new ArrayList<>();
        for(int quantity : partQuantitiesToFind) {
            itemsToFind.add(new Item(partCodeToFind, quantity));
        }

        // Put the items in the warehouse at known locations.
        for(int index = 0; index < itemsToFind.size(); index++) {
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), itemsToFind.get(index));
        }

        // An item for the part to not find.
        Item itemToNotFind = new Item(partCodeToNotFind, MAX_AMOUNT);
        theWarehouse.addToWarehouse(locationToNotFind, itemToNotFind);

        // Make sure that we have the same number of quantities as locations.
        assert partLocationsToFind.size() == partQuantitiesToFind.size();

        // Make sure we aren't storing different parts in the same location.
        assert ! partLocationsToFind.contains(locationToNotFind);
    }

    /**
     * Test finding part locations in an empty warehouse.
     */
    @org.junit.jupiter.api.Test
    void testGetPartLocationsNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(0, actual.size());
    }

    /**
     * Test finding part locations when there is only one location.
     */
    @org.junit.jupiter.api.Test
    void testGetPartLocationsOne()
    {
        Location aLocation = partLocationsToFind.get(0);
        Item anItem = itemsToFind.get(0);
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, anItem);

        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(1, actual.size());
        assertEquals(aLocation, actual.get(0));
    }

    /**
     * Test finding part locations when there are multiple.
     */
    @org.junit.jupiter.api.Test
    void testGetPartLocationsMultiple()
    {
        List<Location> expected = new ArrayList<>(partLocationsToFind);
        expected.add(locationToNotFind);
        List<Location> actual = theWarehouse.getPartLocations();
        assertEquals(expected.size(), actual.size());
        for(Location aLocation : expected) {
            assertTrue(expected.contains(aLocation));
        }
    }

    /**
     * Test finding parts where there are none.
     */
    @org.junit.jupiter.api.Test
    void testGetItemAtNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        for (Location aLocation : partLocationsToFind) {
            Item actual = theWarehouse.getItemAt(aLocation);
            assertNull(actual);
        }
    }

    /**
     * Test finding the partToFind at all of its locations.
     */
    @org.junit.jupiter.api.Test
    void testGetItemAt()
    {
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            Location aLocation = partLocationsToFind.get(index);
            Item expected = itemsToFind.get(index);
            Item actual = theWarehouse.getItemAt(aLocation);
            assertEquals(expected.getPartCode(), actual.getPartCode());
            assertEquals(expected.getQuantity(), actual.getQuantity());
        }
    }

    /**
     * Test getPartCount when there are no parts.
     */
    @org.junit.jupiter.api.Test
    void testGetPartCountNone()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int expectedCount = 0;
        int actualCount = theWarehouse.getPartCount(partCodeToFind);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test getPartCount when there is only one item in the warehouse.
     */
    @org.junit.jupiter.api.Test
    void testGetPartCountOneLocation()
    {
        int expectedCount = 3;
        int anotherPartCode = partCodeToFind + 1;
        Location aLocation = new Location(4, 4);
        Item anItem = new Item(anotherPartCode, expectedCount);
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, anItem);
        int actualCount = theWarehouse.getPartCount(anotherPartCode);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test getPartCount for multiple locations.
     */
    @org.junit.jupiter.api.Test
    void testGetPartCountMultipleLocations()
    {
        int expectedCount = 0;
        for(int quantity : partQuantitiesToFind) {
            expectedCount += quantity;
        }
        int actualCount = theWarehouse.getPartCount(partCodeToFind);
        assertEquals(expectedCount, actualCount);
    }

    /**
     * Test findPart when it is in one location.
     */
    @org.junit.jupiter.api.Test
    void testFindPartOne()
    {
        Location aLocation = new Location(4, 4);
        Item anItem = new Item(partCodeToFind, 1);
        List<Location> expected = new ArrayList<>();
        expected.add(aLocation);

        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(aLocation, anItem);

        List<Location> actual = theWarehouse.findPart(partCodeToFind);
        assertEquals(expected.size(), actual.size());
        assertEquals(aLocation, actual.get(0));
    }

    /**
     * Test findPart when the part is in multiple locations.
     */
    @org.junit.jupiter.api.Test
    void testFindPartMultiple()
    {
        List<Location> expected = new ArrayList<>(partLocationsToFind);
        List<Location> actual = theWarehouse.findPart(partCodeToFind);
        assertEquals(expected.size(), actual.size());
        for(Location aLocation : expected) {
            assertTrue(actual.contains(aLocation));
        }
    }

    /**
     * Test canBeFilled when the warehouse is empty.
     */
    @Test
    void testCanBeFilledEmptyWarehouse()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Item theItem = new Item(partCodeToFind, 1);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual);
    }

    /**
     * Test canBeFilled when the part does not exist.
     */
    @Test
    void testCanBeFilledNoPart()
    {
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Item theItem = new Item(partCodeToFind + 1, 1);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual);
    }

    /**
     * Test canBeFilled for an order of a single part.
     */
    @Test
    void testCanBeFilledOnePart()
    {
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Item theItem = new Item(partCodeToFind, 1);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual);
    }

    /**
     * Test can be filled when the parts are in multiple locations.
     */
    @Test
    void testCanBeFilledMultipleLocations()
    {
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        int quantity = partQuantitiesToFind.get(partQuantitiesToFind.size() - 1) + 1;
        Item theItem = new Item(partCodeToFind, quantity);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual);
    }

    /**
     * Test canBeFilled when all the available items are required.
     */
    @Test
    void testCanBeFilledAllLocations()
    {
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        int maxQuantity = 0;
        for(int quantity : partQuantitiesToFind) {
            maxQuantity += quantity;
        }
        Item theItem = new Item(partCodeToFind, maxQuantity);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertTrue(actual);
    }

    /**
     * Test can be filled when there are insufficient parts.
     */
    @Test
    void testCanBeFilledTooMany()
    {
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        int maxQuantity = 0;
        for(int quantity : partQuantitiesToFind) {
            maxQuantity += quantity;
        }
        Item theItem = new Item(partCodeToFind, maxQuantity + 1);
        inventory.addItem(theItem);
        boolean actual = theWarehouse.canBeFilled(order);
        assertFalse(actual);
    }

    /**
     * Test restocking with an empty inventory.
     */
    @Test
    void testRestockEmptyInventory()
    {
        PartsInventory inventory = new PartsInventory();
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNull(order, "No order should be created when there are no parts in the inventory.");
    }

    /**
     * Test restocking with nothing needed restocking.
     */
    @Test
    void testRestockNothingNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        PartsInventory inventory = new PartsInventory();
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            int partCode = rand.nextInt(1000);
            Item item = new Item(partCode, MAX_AMOUNT);
            inventory.addPart(new Part(partCode, "Part " + partCode,
                    "Manufacturer: " + partCode, "Description: " + partCode, 9.99));
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), item);
        }
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNull(order, "No order should be created when no parts are needed.");
    }

    /**
     * Test restocking with nothing needed restocking.
     */
    @Test
    void testRestockOneNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        PartsInventory inventory = new PartsInventory();
        int partCode = rand.nextInt(1000);
        inventory.addPart(new Part(partCode, "Part " + partCode,
                "Manufacturer: " + partCode, "Description: " + partCode, 9.99));

        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNotNull(order, "An order should be created when a part is needed.");
        assertEquals(1, order.getItemInventory().getItems().size());
        assertEquals(RESTOCK_QUANTITY, order.getItemInventory().getItems().iterator().next().getQuantity());
        assertEquals(partCode, order.getItemInventory().getItems().iterator().next().getPartCode());
    }

    /**
     * Test restocking with multiple needing restocking.
     */
    @Test
    void testRestockMultipleNeeded()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        PartsInventory inventory = new PartsInventory();
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            int partCode = rand.nextInt(1000);
            inventory.addPart(new Part(partCode, "Part " + partCode,
                    "Manufacturer: " + partCode, "Description: " + partCode, 9.99));
        }
        PurchaseOrder order = theWarehouse.createRestockOrder(inventory);
        assertNotNull(order, "An order should be created when multiple parts are needed.");
        assertEquals(partLocationsToFind.size(), order.getItemInventory().getItems().size());
        for(Item anItem : order.getItemInventory().getItems()) {
            assertEquals(RESTOCK_QUANTITY, anItem.getQuantity());
        }
    }

    /**
     * Test creating a purchase order when there is one item missing
     * from a client's order.
     */
    @Test
    void testCreatePurchaseOrderOneItem()
    {
        int numberMissing = 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Item theItem = new Item(partCodeToFind, 1);
        inventory.addItem(theItem);
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        assertEquals(numberMissing, purchaseOrder.getItemInventory().getItems().size());
        assertEquals(partCodeToFind, purchaseOrder.getItemInventory().getItems().iterator().next().getPartCode());
        assertEquals(numberMissing, purchaseOrder.getItemInventory().getItems().iterator().next().getQuantity());
    }

    /**
     * Test creating a purchase order when there are multiple items missing
     * a single part from a client's order.
     */
    @Test
    void testCreatePurchaseOrderMultipleItemsOneMissing()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberMissing = 1;
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        for(int index = 0; index < partQuantitiesToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), new Item(partCode, partQuantitiesToFind.get(index)));
            Item theItem = new Item(partCode, partQuantitiesToFind.get(index) + numberMissing);
            inventory.addItem(theItem);
        }
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        Collection<Item> items = purchaseOrder.getItemInventory().getItems();
        assertEquals(partQuantitiesToFind.size(), items.size());
        for(Item anItem : items) {
            assertEquals(numberMissing, anItem.getQuantity());
        }
    }

    /**
     * Test creating a purchase order when there are multiple items missing
     * the maximum amount from a client's order.
     */
    @Test
    void testCreatePurchaseOrderMultipleItemsMaximumMissing()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberMissing = MAX_AMOUNT;
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        for(int index = 0; index < partQuantitiesToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(partLocationsToFind.get(index), new Item(partCode, partQuantitiesToFind.get(index)));
            Item theItem = new Item(partCode, partQuantitiesToFind.get(index) + numberMissing);
            inventory.addItem(theItem);
        }
        PurchaseOrder purchaseOrder = theWarehouse.createPurchaseOrder(order);
        assertNotNull(purchaseOrder, "A purchase order should be created.");
        Collection<Item> items = purchaseOrder.getItemInventory().getItems();
        assertEquals(partQuantitiesToFind.size(), items.size());
        for(Item anItem : items) {
            assertEquals(numberMissing, anItem.getQuantity());
        }
    }

    /**
     * Create a pick list that leaves one part left.
     */
    @Test
    void testCreatePickListOneItem()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberLeft = 1;
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Location theLocation = partLocationsToFind.get(0);
        int theQuantity = 2 + rand.nextInt(10);
        Item theWarehouseItem = new Item(partCodeToFind, theQuantity);
        Item theClientItem = new Item(partCodeToFind, theQuantity - numberLeft);
        theWarehouse.addToWarehouse(theLocation, theWarehouseItem);
        order.getItemInventory().addItem(theClientItem);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(1, pickList.size());
        PickListItem thePickItem = pickList.get(0);
        assertEquals(partCodeToFind, thePickItem.theItem().getPartCode());
        assertEquals(theQuantity - numberLeft, thePickItem.theItem().getQuantity());
        assertEquals(theLocation, thePickItem.theLocation());
        Item remainingInWarehouse = theWarehouse.getItemAt(theLocation);
        assertNotNull(remainingInWarehouse);
        assertEquals(numberLeft, remainingInWarehouse.getQuantity());
    }


    /**
     * Create a pick list that takes all of a single part.
     */
    @Test
    void testCreatePickListOneItemNoneLeft()
    {
        theWarehouse = new Warehouse(numRows, numColumns);
        int numberLeft = 0;
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Location theLocation = partLocationsToFind.get(0);
        int theQuantity = 2 + rand.nextInt(10);
        Item theWarehouseItem = new Item(partCodeToFind, theQuantity);
        Item theClientItem = new Item(partCodeToFind, theQuantity - numberLeft);
        theWarehouse.addToWarehouse(theLocation, theWarehouseItem);
        order.getItemInventory().addItem(theClientItem);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(1, pickList.size());
        PickListItem thePickItem = pickList.get(0);
        assertEquals(partCodeToFind, thePickItem.theItem().getPartCode());
        assertEquals(theQuantity - numberLeft, thePickItem.theItem().getQuantity());
        assertEquals(theLocation, thePickItem.theLocation());
        Item remainingInWarehouse = theWarehouse.getItemAt(theLocation);
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
    }

    /**
     * Create a pick list from multiple locations.
     */
    @Test
    void testCreatePickListMultipleLocations()
    {
        int theQuantity = MAX_AMOUNT + MAX_AMOUNT/2;
        theWarehouse = new Warehouse(numRows, numColumns);
        theWarehouse.addToWarehouse(partLocationsToFind.get(0), new Item(partCodeToFind, MAX_AMOUNT));
        theWarehouse.addToWarehouse(partLocationsToFind.get(1), new Item(partCodeToFind, theQuantity - MAX_AMOUNT));
        // The client's inventory of items.
        ItemInventory inventory = new ItemInventory();
        ClientOrder order = new ClientOrder(1, customerCode, orderDate, inventory, false);
        Item theClientItem = new Item(partCodeToFind, theQuantity);
        order.getItemInventory().addItem(theClientItem);
        List<PickListItem> pickList = theWarehouse.createAPickList(order);
        assertEquals(2, pickList.size());
        int totalPicked = 0;
        for (PickListItem thePickItem : pickList) {
            int pickedQuantity = thePickItem.theItem().getQuantity();
            assertEquals(partCodeToFind, thePickItem.theItem().getPartCode());
            assertTrue(pickedQuantity == MAX_AMOUNT || pickedQuantity == theQuantity - MAX_AMOUNT);
            totalPicked += pickedQuantity;
            assertTrue(thePickItem.theLocation().equals(partLocationsToFind.get(0)) ||
                    thePickItem.theLocation().equals(partLocationsToFind.get(1)));
        }
        assertEquals(theQuantity, totalPicked);
        Item remainingInWarehouse = theWarehouse.getItemAt(partLocationsToFind.get(0));
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
        remainingInWarehouse = theWarehouse.getItemAt(partLocationsToFind.get(1));
        assertNull(remainingInWarehouse, "Empty locations in the warehouse must be null.");
    }

    /**
     * Test storeDelivery adding 1 part to each existing location.
     */
    @Test
    void testStoreDeliveryOneMore()
    {
        int quantityDelivered = 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        ItemInventory inventory = new ItemInventory();
        Delivery theDelivery = new Delivery(1, orderDate, inventory, false);
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            int partCode = index + 10;
            theWarehouse.addToWarehouse(
                    partLocationsToFind.get(index),
                    new Item(partCode, partQuantitiesToFind.get(index)));
            inventory.addItem(new Item(partCode, quantityDelivered));
        }
        theWarehouse.storeDelivery(theDelivery);
        for(int index = 0; index < partLocationsToFind.size(); index++) {
            Item theItem = theWarehouse.getItemAt(partLocationsToFind.get(index));
            assertEquals(partQuantitiesToFind.get(index) + quantityDelivered, theItem.getQuantity());
        }
    }

    /**
     * Test storeDelivery adding to multiple locations.
     */
    @Test
    void testStoreDeliveryInMultipleLocations()
    {
        int originalQuantity = 1;
        int quantityDelivered = 2 * MAX_AMOUNT + 1;
        theWarehouse = new Warehouse(numRows, numColumns);
        ItemInventory inventory = new ItemInventory();
        Delivery theDelivery = new Delivery(1, orderDate, inventory, false);
        int partCode = partCodeToFind;
        Location originalLocation = partLocationsToFind.get(0);
        theWarehouse.addToWarehouse(originalLocation, new Item(partCode, originalQuantity));
        inventory.addItem(new Item(partCode, quantityDelivered));
        theWarehouse.storeDelivery(theDelivery);
        List<Location> currentLocations = theWarehouse.findPart(partCode);
        assertEquals(3, currentLocations.size());
        Item updatedItem = theWarehouse.getItemAt(originalLocation);
        assertNotNull(updatedItem);
        assertEquals(MAX_AMOUNT, updatedItem.getQuantity());
        int totalStored = 0;
        for(Location aLocation : currentLocations) {
            totalStored += theWarehouse.getItemAt(aLocation).getQuantity();
        }
        assertEquals(originalQuantity + quantityDelivered, totalStored);
    }

}