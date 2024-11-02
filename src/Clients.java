import java.util.*;

/**
 * All the clients.
 */
public class Clients
{
    private final Map<Integer, Client> clients = new TreeMap<>();

    /**
     * Add a client.
     * @param aClient The client to be added.
     */
    public void addClient(Client aClient) {
        clients.put(aClient.customerCode(), aClient);
    }

    /**
     * Print the clients.
     */
    public void printClients() {
        for (Client aClient : clients.values()) {
            System.out.println(aClient);
        }
    }
}

