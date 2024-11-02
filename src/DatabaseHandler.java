import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Handle interactions with the database.
 * TODO: Complete the readProductTypes method in this class for part 2.
 */
public class DatabaseHandler
{
    private static final int ROWS = 20, COLS = 30;
    private final Connection connection;
    private final Warehouse warehouse;
    private final Clients clients;
    private final PartsInventory partsInventory;
    private final ClientOrders clientOrders;
    private final PurchaseOrders purchaseOrders;
    private final Deliveries theDeliveries;

    /**
     * Create a database reader and read the contents of
     * most of the tables.
     *
     * @param userDB   The database and username.
     * @param password The password.
     * @throws ClassNotFoundException If there is no Database driver.
     * @throws SQLException           If there is a problem reading from the database.
     */
    public DatabaseHandler(String userDB, String password)
            throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Set a timeout in case of connectivity issues.
        DriverManager.setLoginTimeout(5);
        connection =
                DriverManager.getConnection(
                        "jdbc:mysql://dragon.kent.ac.uk/" +
                                userDB, userDB, password);

        warehouse = readWarehouse(connection);
        clients = readClients(connection);
        partsInventory = readParts(connection);
        clientOrders = readClientOrders(connection);
        purchaseOrders = readPurchaseOrders(connection);
        theDeliveries = readDeliveries(connection);

    }

    /**
     * TODO: Part 2.
     * Get the product types from the database and return them
     * as a map with type as the key and type description as the value.
     *
     * @return the product types.
     */
    public Map<String, String> readProductTypes()
            throws SQLException
    {
        // Initialize a TreeMap to store product types and descriptions
        Map<String, String> types = new TreeMap<>();

        // Create a statement for executing SQL queries
        Statement statement = connection.createStatement();

        // Execute the SQL query to retrieve product types from the database
        ResultSet resultSet = statement.executeQuery("SELECT * FROM productTypes");

        // Iterate over the result set and populate the map with product types and descriptions
        while(resultSet.next()){
            String type = resultSet.getString("type").trim();
            String typeDescription = resultSet.getString("typeDescription").trim();
            types.put(type, typeDescription);
        }

        // Close the result set and statement to release resources
        resultSet.close();
        statement.close();

        return types; // Return the map containing product types and descriptions
    }

    /**
     * Get the database connection.
     * This should not be needed.
     *
     * @return the connection.
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Get the warehouse.
     *
     * @return the warehouse.
     */
    public Warehouse getWarehouse()
    {
        return warehouse;
    }

    /**
     * Get all the clients.
     *
     * @return the clients.
     */
    public Clients getClients()
    {
        return clients;
    }

    /**
     * Get all the parts.
     *
     * @return the parts.
     */
    public PartsInventory getPartsInventory()
    {
        return partsInventory;
    }

    /**
     * Get all the client orders.
     *
     * @return the client orders.
     */
    public ClientOrders getClientOrders()
    {
        return clientOrders;
    }

    /**
     * Get all the purchase orders.
     *
     * @return the purchase orders.
     */
    public PurchaseOrders getPurchaseOrders()
    {
        return purchaseOrders;
    }

    /**
     * Update the warehouse table from the current state.
     * @deprecated The database is read-only.
     * @param theWarehouse The warehouse.
     */
    public void updateWarehouse(Warehouse theWarehouse)
    {
        try {
            Statement statement = connection.createStatement();
            // Clear the existing contents of the warehouse table.
            statement.executeUpdate("delete from warehouse");
            // Insert each item into the warehouse at its location.
            List<Location> partLocations = theWarehouse.getPartLocations();
            for (Location theLocation : partLocations) {
                Item anItem = theWarehouse.getItemAt(theLocation);
                if (statement.executeUpdate(
                        String.format("insert into warehouse values (\"%d,%d\", %d, %d)",
                                theLocation.row(), theLocation.col(),
                                anItem.getPartCode(), anItem.getQuantity())) != 1) {
                    System.err.println("Failed to insert " + anItem + " into the warehouse.");
                }
            }


            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to update the warehouse: " + e);
        }
    }

    /**
     * Insert the purchase order into the purchaseOrders table.
     * @deprecated The database is read-only.
     * @param order The order to be inserted.
     * @throws SQLException
     */
    public void insertPurchaseOrder(PurchaseOrder order) throws SQLException
    {
        Statement statement = connection.createStatement();
        if (statement.executeUpdate(
                String.format("insert into purchaseOrders values (%d, \"%s\", %s)",
                        order.getOrderNumber(), order.getDateOrdered(),
                        order.isFulfilled() ? "'Y'" : "'N'")) == 1) {
            for (Item anItem : order.getItemInventory().getItems()) {
                if (statement.executeUpdate(
                        String.format("insert into purchaseOrderItems values (%d, \"%s\", %s)",
                                order.getOrderNumber(), anItem.getPartCode(), anItem.getQuantity())) != 1) {
                    System.err.println("Failed to insert into purchaseOrderItems");
                }
            }
        } else {
            System.err.println("Failed to insert into purchaseOrders");
        }
    }

    /**
     * Set the fulfilled flag n the orders table to 'Y'.
     * @deprecated The database is read-only.
     * @param orderNumber the client's order number.
     * @throws SQLException
     */
    public void setClientOrderFulfilled(int orderNumber) throws SQLException
    {
        setFulfilled("clientOrders", "orderNumber", orderNumber);
    }

    /**
     * Set the fulfilled flag n the deliveries table to 'Y'.
     * @deprecated The database is read-only.
     * @param deliveryNumber the delivery number.
     * @throws SQLException
     */
    public void setDeliveryFulfilled(int deliveryNumber) throws SQLException
    {
        setFulfilled("deliveries", "deliveryNumber", deliveryNumber);
    }

    /**
     * Set the fulfilled flag in the purchases table to 'Y'.
     * @deprecated The database is read-only.
     * @param purchaseOrderNumber the purchase order number.
     * @throws SQLException
     */
    public void setPurchaseOrderFulfilled(int purchaseOrderNumber) throws SQLException
    {
        setFulfilled("purchases", "purchaseOrderNumber", purchaseOrderNumber);
    }

    /**
     * Set the flag to Y for where the given column has the matching number.
     * @deprecated The database is read-only.
     * @param table      The table to update.
     * @param columnName The column to match.
     * @param number     The value in the column.
     * @throws SQLException
     */
    private void setFulfilled(String table, String columnName, int number)
            throws SQLException
    {
        Statement statement = connection.createStatement();
        statement.executeUpdate(
                String.format("update %s set flag = 'Y' where %s = %d",
                        table, columnName, number));
        statement.close();
    }

    /**
     * Get all the deliveries.
     *
     * @return the deliveries.
     */
    public Deliveries getTheDeliveries()
    {
        return theDeliveries;
    }

    /**
     * Read the contents of each warehouse section
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException On SQL error.
     */
    private Warehouse readWarehouse(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        Warehouse warehouse = new Warehouse(ROWS, COLS);
        ResultSet resultSet =
                statement.executeQuery("select * from warehouse");
        while (resultSet.next()) {
            String location = resultSet.getString("location").trim();
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            String[] rowCol = location.split(",");
            Location theLocation =
                    new Location(Integer.parseInt(rowCol[0]),
                            Integer.parseInt(rowCol[1]));
            Item theItem = new Item(partCode, quantity);
            warehouse.addToWarehouse(theLocation, theItem);
        }
        resultSet.close();
        statement.close();
        return warehouse;
    }

    /**
     * Read the client information; name, address etc.
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException on SQL error.
     */
    private Clients readClients(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        Clients clients = new Clients();
        ResultSet resultSet =
                statement.executeQuery("select * from clients");
        while (resultSet.next()) {
            int customerCode = resultSet.getInt("customerCode");
            String businessName = resultSet.getString("businessName").trim();
            String vatNumber = resultSet.getString("vatNumber").trim();
            String email = resultSet.getString("email").trim();
            String phoneNumber = resultSet.getString("phoneNumber").trim();
            String address = resultSet.getString("address").trim();
            Client aClient = new Client(customerCode, businessName, vatNumber, email, phoneNumber, address);
            clients.addClient(aClient);
        }
        resultSet.close();
        statement.close();
        return clients;

    }

    /**
     * Read all the parts that the company sells (which may or may not be in stock)
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException on SQL error.
     */
    private PartsInventory readParts(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        PartsInventory partsInventory = new PartsInventory();
        ResultSet resultSet =
                statement.executeQuery(
                        "select * from parts, productTypes where parts.type = productTypes.type");
        while (resultSet.next()) {
            int partCode = resultSet.getInt("partCode");
            String type = resultSet.getString("typeDescription").trim();
            String manufacturer = resultSet.getString("manufacturer").trim();
            String description = resultSet.getString("description").trim();
            double price = resultSet.getDouble("price");
            Part aPart = new Part(partCode, type, manufacturer, description, price);
            partsInventory.addPart(aPart);
        }
        resultSet.close();
        statement.close();
        return partsInventory;
    }

    /**
     * Read all outstanding client orders that have not been picked yet
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException on SQL error.
     */
    private ClientOrders readClientOrders(Connection connection)
            throws SQLException
    {
        Statement statement = connection.createStatement();
        ClientOrders clientOrders = new ClientOrders();
        ResultSet resultSet = statement.executeQuery("select * from clientOrders, orderItems where clientOrders.orderNumber = orderItems.orderNumber order by clientOrders.orderNumber");
        ItemInventory itemInventory = null;
        int previousOrder = 0;
        while (resultSet.next()) {
            int orderNumber = resultSet.getInt("orderNumber");
            int customerCode = resultSet.getInt("customerCode");
            String dateOrdered = resultSet.getString("dateOrdered");
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            String fulfilled = resultSet.getString("flag");
            if (orderNumber != previousOrder) {
                // A new order.
                itemInventory = new ItemInventory();
                ClientOrder anOrder =
                        new ClientOrder(orderNumber, customerCode,
                                new Date(dateOrdered), itemInventory,
                                fulfilled.equals("Y"));
                clientOrders.addOrder(anOrder);
                previousOrder = orderNumber;
            }
            Item anItem = new Item(partCode, quantity);
            itemInventory.addItem(anItem);
        }
        resultSet.close();
        statement.close();
        return clientOrders;
    }

    /**
     * Read all outstanding purchase orders that have not been placed yet
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException on SQL error.
     */
    private PurchaseOrders readPurchaseOrders(Connection connection)
            throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from purchaseOrders, purchaseOrderItems where purchaseOrders.purchaseOrderNumber = purchaseOrderItems.purchaseOrderNumber order by purchaseOrders.purchaseOrderNumber");
        ItemInventory itemInventory = null;
        PurchaseOrders purchaseOrders = new PurchaseOrders();
        int previousOrder = 0;
        while (resultSet.next()) {
            int orderNumber = resultSet.getInt("purchaseOrderNumber");
            String dateOrdered = resultSet.getString("dateOrdered");
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            String fulfilled = resultSet.getString("flag");
            if (orderNumber != previousOrder) {
                // A new order.
                itemInventory = new ItemInventory();
                PurchaseOrder anOrder = new PurchaseOrder(orderNumber,
                        new Date(dateOrdered), itemInventory,
                        fulfilled.equals("Y"));
                purchaseOrders.addOrder(anOrder);
                previousOrder = orderNumber;
            }
            Item anItem = new Item(partCode, quantity);
            itemInventory.addItem(anItem);
        }
        resultSet.close();
        statement.close();
        return purchaseOrders;

    }

    /**
     * Read all recent deliveries that have not been put into the warehouse yet
     *
     * @param connection Execute a query with this connection.
     * @throws SQLException on SQL error.
     */
    private Deliveries readDeliveries(Connection connection)
            throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from deliveries, deliveryItems where deliveries.deliveryNumber = deliveryItems.deliveryNumber order by deliveries.deliveryNumber");
        ItemInventory itemInventory = null;
        Deliveries deliveries = new Deliveries();
        int previousDelivery = 0;
        while (resultSet.next()) {
            int deliveryNumber = resultSet.getInt("deliveryNumber");
            String dateDelivered = resultSet.getString("dateDelivered");
            int partCode = resultSet.getInt("partCode");
            int quantity = resultSet.getInt("quantity");
            boolean inTheWarehouse = resultSet.getString("flag").equals("Y");
            if (deliveryNumber != previousDelivery) {
                // A new order.
                itemInventory = new ItemInventory();
                Delivery aDelivery =
                        new Delivery(deliveryNumber, new Date(dateDelivered),
                                itemInventory, inTheWarehouse);
                deliveries.addDelivery(aDelivery);
                previousDelivery = deliveryNumber;
            }
            Item anItem = new Item(partCode, quantity);
            itemInventory.addItem(anItem);
        }
        resultSet.close();
        statement.close();
        return deliveries;
    }

}
