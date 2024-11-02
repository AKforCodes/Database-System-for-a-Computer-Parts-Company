import java.util.Comparator;
import java.util.Objects;

/**
 * Details of a client.
 * @param customerCode Identification code.
 * @param businessName Name of the business.
 * @param vatNumber VAT number.
 * @param email Email address.
 * @param phoneNumber Phone number.
 * @param address Business address.
 */
public record Client(int customerCode, String businessName, String vatNumber,
                     String email, String phoneNumber, String address) implements Comparable<Client> {
    @Override
    public String toString() {
        return (businessName+", "+address);
    }

    @Override
    public int compareTo(Client that) {
        return Objects.compare(this, that,
                Comparator.comparing(Client::customerCode));
    }
}
