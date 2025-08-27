import java.time.format.DateTimeFormatter;

/**
 * Utility class that centralizes date/time format patterns so they are consistent across
 * the entire application.
 */
public final class DateTimeFormat {
    private DateTimeFormat() {}

    public static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma");
}