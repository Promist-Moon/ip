package locky.app;

import java.util.Objects;

import locky.error.LockyException;
import locky.tasks.TaskList;
import locky.utils.Parser;
import locky.utils.Storage;

/**
 * Main entry point and controller for the Locky chatbot.
 */
public class Locky {
    private final TaskList list;

    /**
     * Creates a new Locky.app.Locky instance using the specified file path
     * for persistent storage of tasks.
     *
     * @param filePath the file path where tasks are stored and loaded.
     */
    public Locky(String filePath) {
        assert filePath != null && !filePath.isBlank() : "filePath must be non-empty";
        Storage storage = new Storage(filePath);
        this.list = new TaskList(storage);
    }

    /**
     * Returns greeting onboarding message.
     * @return greeting String
     */
    public String getGreeting() {
        return "Hello! I'm Locky\n"
                + "Reminding you to Lock In!\n"
                + "What can I do for you?\n";
    }

    /**
     * Handles user input as a String by returning
     * a String response by Locky.
     * @param input String
     * @return String response retrieved from handleLineToString
     */
    public String getResponse(String input) {
        assert input != null : "input must not be null";

        if (Objects.equals(input, "bye")) {
            return "You better Lock In!\n";
        }

        try {
            return handleLineToString(input);
        } catch (LockyException e) {
            return e.getMessage() + "\n";
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage() + "\n";
        }
    }

    /**
     * Parses and executes a single line of user input as a Locky command.
     * @param taskString raw user input
     * @return formatted string containing Locky's response to the command
     * @throws LockyException if arguments are empty or invalid
     */
    private String handleLineToString(String taskString) throws LockyException {
        var cmd = Parser.parse(taskString);
        try {
            return cmd.execute(list);
        } catch (java.io.IOException ioe) {
            return "(Warning: failed to save: " + ioe.getMessage() + ")\n";
        }
    }
}
