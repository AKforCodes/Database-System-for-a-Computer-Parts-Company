import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Model a date in the format stored in the database.
 */
public class Date
{
    // Used to format Calendar objects.
    private static final SimpleDateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Calendar date;

    /**
     * Create a Date.
     * @param dayAndTime A date in the form yyyy-mm-dd hh:mm:ss.
     */
    public Date(String dayAndTime)
    {
        this.date = decodeDate(dayAndTime);
    }

    /**
     * Copy constructor.
     * @param other Date to be copied.
     */
    public Date(Date other)
    {
        this.date = (Calendar) other.date.clone();
    }

    /**
     * Create a date from the given calendar.
     * @param calendar The date.
     */
    public Date(Calendar calendar)
    {
        this.date = calendar;
    }

    /**
     * Check whether this date is before the other.
     * @param other Another date.
     * @return true if this is before the other.
     */
    public boolean before(Date other)
    {
        return date.before(other.date);
    }

    /**
     * Check whether this date is after the other.
     * @param other Another date.
     * @return true if this is after the other.
     */
    public boolean after(Date other)
    {
        return date.after(other.date);
    }

    /**
     * Get a date corresponding to the current time and date.
     * @return The current time and date.
     */
    public static Date getNow()
    {
        return new Date(new GregorianCalendar(TimeZone.getTimeZone("GMT")));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date1 = (Date) o;
        return Objects.equals(date, date1.date);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(date);
    }

    /**
     * Format a date in Calendar form.
     * @return The formatted date in dd-mm-yyyy format.
     */
    @Override
    public String toString()
    {
        return formatter.format(date.getTime());
    }

    /**
     * Decode a date/time string in the form yyyy-mm-dd hh:mm:ss.
     * @param dateAndTime The date and time.
     * @return a calendar based on the date.
     */
    private Calendar decodeDate(String dateAndTime) {
        String[] parts = dateAndTime.split(" ");
        String[] date = parts[0].split("-");
        String[] time = parts[1].split(":");
        return new GregorianCalendar(Integer.parseInt(date[0]),
                Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]),
                Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
    }
}
