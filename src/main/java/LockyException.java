/**
 * Represents a custom checked exception used within the Locky application.
 * A {@code LockyException} indicates that an error has occurred during
 * user command processing or task management, such as invalid input,
 * referencing a non-existent task, or providing a malformed date/time.
 */
public class LockyException extends Exception {
    public LockyException(String message) {
        super(message);
    }
}
