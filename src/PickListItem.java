/**
 * An item on a pick list.
 * @param theLocation Where to source the item.
 * @param theItem The part and the quantity to pick.
 */
public record PickListItem(Location theLocation, Item theItem)
{
    @Override
    public String toString()
    {
        return String.format("Location: %s Item: %s", theLocation, theItem);
    }
}
