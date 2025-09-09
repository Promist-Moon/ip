package locky.commands;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import locky.error.LockyException;
import locky.tasks.TaskList;
import locky.utils.Parser;

/**
 * Represents the {@code event} command.
 * When executed, it adds a new Event task with the given description,
 * start time, and end time into the TaskList.
 */
public class EventCommand implements Command {
    private final String args;
    public EventCommand(String args) {
        this.args = args;
    }

    @Override
    public String execute(TaskList list) throws LockyException, IOException {
        Parser.ParsedEvent pe;
        try {
            pe = Parser.parseEventArgs(args);
        } catch (Exception e) {
            if (e instanceof DateTimeParseException) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            }
            throw e;
        }
        list.addEvent(pe.description(), pe.start(), pe.end());
        return "Added: " + list.getTask(list.getSize()) + "\n";
    }
}
