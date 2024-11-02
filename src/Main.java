import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.*;

/**
 * Main class for the A2 assignment.
 * The main method connects to the database and retrieves
 * objects that store all the data from the database.
 * You do not need to add anything to this class.
 */
public class Main
{
    // Database name and username.
    private static final String userDB = "comp5200";
    // Database password.
    private static final String password = "pa33word";
    private static final Random rand = new Random();

    // Access to the database.
    private static DatabaseHandler databaseHandler = null;

    /**
     * Read tables from the database and support some operations on the data.
     *
     * @param args Not used.
     */
    public static void main(String[] args)
    {
        try {
            databaseHandler = new DatabaseHandler(userDB, password);
            //printDetails(reader);
        } catch (CommunicationsException |
                 SQLTimeoutException e) {
            System.err.println("Failed to connect to the database. Make sure you are connected to the VPN if you are not on campus.");
            System.exit(1);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to access the database: " + e);
            System.exit(1);
        }

        Warehouse theWarehouse = databaseHandler.getWarehouse();
        ClientOrders clientOrders = databaseHandler.getClientOrders();
        PartsInventory partsInventory = databaseHandler.getPartsInventory();

        part1(theWarehouse);
        // TODO: Uncomment each of the following calls when you are ready to start that part.
          part2(databaseHandler);
          part3(partsInventory, clientOrders);
          part4(theWarehouse, clientOrders);
          part5(theWarehouse, partsInventory, databaseHandler.getPurchaseOrders());
          part6(theWarehouse, clientOrders, databaseHandler.getPurchaseOrders());
          part7(theWarehouse, clientOrders);
          part8(theWarehouse, databaseHandler.getTheDeliveries());
    }

    /**
     * Test completion of part 1 of the assignment.
     *
     * @param theWarehouse The contents of the warehouse.
     */
    public static void part1(Warehouse theWarehouse)
    {
        System.out.println("Part 1 tests");
        // Print the locations that contain at least one part.
        List<Location> partLocations = theWarehouse.getPartLocations();
        System.out.println("Part1a: Results of running getPartLocations: " + partLocations);

        // Print details of the part at a random location.
        System.out.print("Part1b: Results of running getItemAt: ");
        if (!partLocations.isEmpty()) {
            Location theLocation = partLocations.get(rand.nextInt(partLocations.size()));
            Item anItem = theWarehouse.getItemAt(theLocation);
            System.out.printf("At location %s is %s%n", theLocation, anItem);
        } else {
            System.out.println("No parts available.");
        }

        // Print the count of a random part that is in the warehouse.
        List<Integer> availableParts = theWarehouse.getAvailablePartCodes();
        // Select a random part code.
        int index = rand.nextInt(availableParts.size());
        int partCode = availableParts.get(index);
        int count = theWarehouse.getPartCount(partCode);
        System.out.printf("Part1c: Results of running getPartCount: There are %d boxes of part %d%n",
                count, partCode);

        // Print the locations of a random part.
        index = rand.nextInt(availableParts.size());
        partCode = availableParts.get(index);
        List<Location> locationsOfPart = theWarehouse.findPart(partCode);
        System.out.printf("Part1d: Results of running findPart: Part %s is at location(s): %s%n",
                partCode, locationsOfPart);

        System.out.println("=== End of part 1");
        System.out.println();
    }

    /**
     * Get the contents of the productTypes table and print the pairs.
     *
     * @param databaseHandler The database handler.
     */
    private static void part2(DatabaseHandler databaseHandler)
    {
        System.out.println("Part 2 tests");
        System.out.println("Results of running getProductTypes: ");
        try {
            Map<String, String> types = databaseHandler.readProductTypes();
            for (String theType : types.keySet()) {
                System.out.printf("%s: %s%n", theType, types.get(theType));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        System.out.println("=== End of part 2");
        System.out.println();
    }

    /**
     * Calculate the cost of each client order.
     *
     * @param partsInventory The parts inventory.
     * @param clientOrders   The client orders.
     */
    private static void part3(PartsInventory partsInventory,
                              ClientOrders clientOrders)
    {
        System.out.println("Part 3 tests");
        List<Integer> orderNumbers = clientOrders.getOrderNumbers();
        System.out.println("Results of running getCost:");
        for (int orderNumber : orderNumbers) {
            ClientOrder theOrder = clientOrders.getOrder(orderNumber);
            double cost = partsInventory.getCost(theOrder);
            System.out.printf("Client order %3d costs £%9.2f%n",
                    orderNumber, cost);
        }
        System.out.println("=== End of part 3");
        System.out.println();
    }

    /**
     * Check which unfilled orders can be filled.
     *
     * @param theWarehouse The warehouse.
     * @param allOrders    The client orders.
     */
    public static void part4(Warehouse theWarehouse, ClientOrders allOrders)
    {
        System.out.println("Part 4 tests");
        List<Integer> orderNumbers = allOrders.getOrderNumbers();
        System.out.println("Results of running canBeFilled:");
        for (int orderNumber : orderNumbers) {
            ClientOrder theOrder = allOrders.getOrder(orderNumber);
            if (!theOrder.isFulfilled()) {
                System.out.printf("Client order %3d: ", orderNumber);
                if (theWarehouse.canBeFilled(theOrder)) {
                    System.out.println("is in stock.");
                } else {
                    System.out.println("needs a purchase order.");
                }
            }
        }

        System.out.println("=== End of part 4");
        System.out.println();
    }

    /**
     * Create a purchase order for any parts not currently in stock.
     *
     * @param theWarehouse   The warehouse.
     * @param partsInventory The parts inventory.
     */
    private static void part5(Warehouse theWarehouse,
                              PartsInventory partsInventory,
                              PurchaseOrders orders)
    {
        System.out.println("Part 5 tests");
        PurchaseOrder theOrder = theWarehouse.createRestockOrder(partsInventory);
        System.out.println("Results of running createRestockOrder: ");
        if (theOrder != null) {
            System.out.printf("%s%n", theOrder);
            orders.addOrder(theOrder);
        } else {
            System.out.println("No items need restocking.");
        }
        System.out.println("=== End of part 5");
        System.out.println();
    }

    /**
     * Generate a purchase order for an unfilled order that cannot be filled.
     * Only order items from the client order for which there are not
     * enough in the warehouse.
     *
     * @param theWarehouse The warehouse.
     * @param allOrders    The client orders.
     */
    public static void part6(Warehouse theWarehouse, ClientOrders allOrders,
                             PurchaseOrders purchaseOrders)
    {
        System.out.println("Part 6 tests");
        List<Integer> orderNumbers = allOrders.getOrderNumbers();
        List<ClientOrder> cannotBeFilled = new ArrayList<>();
        for (int orderNumber : orderNumbers) {
            ClientOrder theClientOrder = allOrders.getOrder(orderNumber);
            if (!theClientOrder.isFulfilled()) {
                if (!theWarehouse.canBeFilled(theClientOrder)) {
                    cannotBeFilled.add(theClientOrder);
                }
            }
        }
        if (!cannotBeFilled.isEmpty()) {
            // Choose one at random.
            ClientOrder theClientOrder = cannotBeFilled.get(rand.nextInt(cannotBeFilled.size()));
            PurchaseOrder purchase = theWarehouse.createPurchaseOrder(theClientOrder);
            purchaseOrders.addOrder(purchase);
            System.out.printf("Client order %d triggered %s%n",
                    theClientOrder.getOrderNumber(), purchase);
            // This is where the purchase order would be inserted into the database.
            // This is not possible as the database is read-only.
        } else {
            System.out.println("No purchase order was generated.");
        }
        System.out.println("=== End of part 6");
        System.out.println();
    }

    /**
     * Generate a pick list for at least one client order that
     * can be fulfilled.
     *
     * @param theWarehouse The warehouse.
     * @param allOrders    The client orders.
     */
    public static void part7(Warehouse theWarehouse, ClientOrders allOrders)
    {
        System.out.println("Part 7 tests");
        List<Integer> orderNumbers = allOrders.getOrderNumbers();
        // Randomise the order.
        Collections.shuffle(orderNumbers);
        // Choose a random customer order.
        System.out.println("Results of running createAPickList:");
        boolean filled = false;
        int index = 0;
        while(! filled && index < orderNumbers.size()) {
            int clientOrderNumber = orderNumbers.get(index);
            ClientOrder theOrder = allOrders.getOrder(clientOrderNumber);
            if (!theOrder.isFulfilled()) {
                if (theWarehouse.canBeFilled(theOrder)) {
                    List<PickListItem> pickList = theWarehouse.createAPickList(theOrder);
                    System.out.printf("Customer order %d filled for customer %d%n",
                            theOrder.getOrderNumber(), theOrder.getCustomerCode());
                    System.out.printf("Pick list is %s%n", pickList);
                    theOrder.setFulfilled();
                    filled = true;
                    // This is where the database would be updated to indicate that the
                    // order is fulfilled, and update the contents of the warehouse.
                    // But the database is read-only.
                }
            }
            index++;
        }
        if(! filled) {
            System.out.println("No order was filled.");
        }
        System.out.println("=== End of part 7");
        System.out.println();
    }

    /**
     * Store the contents of a random delivery in the warehouse.
     *
     * @param theWarehouse  The contents of the warehouse.
     * @param theDeliveries The deliveries.
     */
    public static void part8(Warehouse theWarehouse, Deliveries theDeliveries)
    {
        System.out.println("Part 8 tests");
        List<Integer> outstandingDeliveries =
                theDeliveries.getOutstandingDeliveryNumbers();
        if (!outstandingDeliveries.isEmpty()) {
            // Select a random delivery.
            Delivery toDeliver = theDeliveries.getDelivery(
                    outstandingDeliveries.get(
                            rand.nextInt(outstandingDeliveries.size())));
            // Print the items to be delivered.
            System.out.printf("%s%n", toDeliver);
            Collection<Item> theItems = toDeliver.getItemInventory().getItems();
            // Print the quantities before the delivery.
            for (Item anItem : theItems) {
                int partCode = anItem.getPartCode();
                List<Location> currentLocations = theWarehouse.findPart(partCode);
                if(! currentLocations.isEmpty()) {
                    System.out.printf("Part %d is stored at: ",
                            partCode);
                    for(Location aLocation : currentLocations) {
                        System.out.printf("%s (%d boxes) ", aLocation,
                                theWarehouse.getItemAt(aLocation).getQuantity());
                    }
                    System.out.println();
                }
                else {
                    System.out.printf("Part %d is not currently in the warehouse.%n", partCode);
                }
            }
            List<Location> whereStored = theWarehouse.storeDelivery(toDeliver);
            toDeliver.setInTheWarehouse();
            // Print the quantities after the delivery.
            System.out.println("Quantities in the warehouse after the delivery.");
            for (Item anItem : theItems) {
                int count = theWarehouse.getPartCount(anItem.getPartCode());
                System.out.printf("%d boxes of part %d/ ", count, anItem.getPartCode());
            }
            System.out.println();
            Collections.sort(whereStored);
            for(Location aLocation : whereStored) {
                Item anItem = theWarehouse.getItemAt(aLocation);
                System.out.printf("Part %d was stored at: %s which now contains %d boxes.%n",
                        anItem.getPartCode(), aLocation,
                        theWarehouse.getItemAt(aLocation).getQuantity());

            }
            // This is where the database would be updated to indicate that the
            // order is fulfilled, and update the contents of the warehouse.
            // But the database is read-only.
        } else {
            System.out.println("There are no outstanding deliveries.");
        }
        System.out.println("=== End of part 8");
        System.out.println();
    }

    /**
     * Print details of the tables that were read by the reader.
     *
     * @param reader The database reader.
     */
    private static void printDetails(DatabaseHandler reader)
    {
        Warehouse warehouse = reader.getWarehouse();
        System.out.println("List of all the non-empty warehouse locations and what they contain:");
        warehouse.printOccupiedLocations();
        System.out.println();
        System.out.println("Map of the warehouse.");
        warehouse.printMap();
        System.out.println();

        Clients clients = reader.getClients();
        System.out.println("=== Clients");
        clients.printClients();
        System.out.println();
        PartsInventory partsInventory = reader.getPartsInventory();
        System.out.println("=== Parts");
        partsInventory.printParts();
        ClientOrders clientOrders = reader.getClientOrders();
        System.out.println("=== ClientOrders");
        clientOrders.printOrders();
        PurchaseOrders purchaseOrders = reader.getPurchaseOrders();
        System.out.println("=== PurchaseOrders");
        purchaseOrders.printOrders();
        Deliveries theDeliveries = reader.getTheDeliveries();
        System.out.println("=== Deliveries");
        theDeliveries.printDeliveries();

    }

}
