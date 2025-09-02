package locky.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import locky.utils.DateTimeFormat;

/**
 * Represents a deadline task with a specific due date and time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates a new deadline task with description, completion
     * status, and deadline.
     *
     * @param description text description of the deadline task.
     * @param isDone whether the task is already marked as completed.
     * @param deadline the date and time by which the deadline task is due.
     */
    public Deadline(String description, boolean isDone, LocalDateTime deadline) {
        super(description, isDone);
        this.deadline = Objects.requireNonNull(deadline, "deadline");
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " by: " + getDeadlineFormatted();
    }

    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Returns deadline formatted as a string in
     * Locky.utils.DateTimeFormat's DISPLAY format.
     *
     * @return formatted deadline
     */
    public String getDeadlineFormatted() {
        return DateTimeFormat.DISPLAY.format(deadline);
    }
}
