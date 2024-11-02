/**
 * A location in the warehouse.
 * Items in the warehouse are always stored at a location.
 * @param row The row.
 * @param col The column.
 */
public record Location(int row, int col) implements Comparable<Location>
{
    @Override
    public String toString()
    {
        return String.format("(%d,%d)", row, col);
    }

    @Override
    public int compareTo(Location that)
    {
        return (row*1000 + col) - (that.row * 1000 + col);
    }
}
