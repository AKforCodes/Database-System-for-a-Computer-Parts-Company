/**
 * Details of a part.
 * @param partCode The part code.
 * @param type The type of part.
 * @param manufacturer The part's manufacturer.
 * @param description A description of the part.
 * @param price The price of the part.
 */
public record Part(int partCode, String type, String manufacturer,
                   String description, double price) {
    @Override
    public String toString() {
        return String.format("%s: %s, %s costs £%.2f",
                type, manufacturer, description, price);
    }
}
