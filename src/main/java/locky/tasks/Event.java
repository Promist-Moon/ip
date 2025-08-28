package locky.tasks;

import locky.utils.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an event task with a start and end datetime
 */
public class Event extends Task {

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates a new {@code Locky.tasks.Event} task with the given description, completion
     * status, start time, and end time.
     *
     * @param description the text description of the event.
     * @param isDone whether the event task is already marked as completed.
     * @param start the starting date and time of the event.
     * @param end the ending date and time of the event; must be after {@code start}.
     * @throws IllegalArgumentException if {@code end} is not strictly after {@code start}.
     * @throws NullPointerException if {@code start} or {@code end} is {@code null}.
     */
    public Event(String description, boolean isDone, LocalDateTime start, LocalDateTime end) {
        super(description, isDone);
        this.start = Objects.requireNonNull(start, "start");
        this.end   = Objects.requireNonNull(end, "end");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Locky.tasks.Event end must be after start.");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " from: " + getStartFormatted() + " to: " + getEndFormatted();
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Returns the event's start time formatted for display.
     *
     * @return the formatted start date/time of the event.
     */
    public String getStartFormatted() {
        return DateTimeFormat.DISPLAY.format(start);
    }

    /**
     * Returns the event's end time formatted for display.
     *
     * @return the formatted end date/time of the event.
     */
    public String getEndFormatted()   {
        return DateTimeFormat.DISPLAY.format(end);
    }
}
