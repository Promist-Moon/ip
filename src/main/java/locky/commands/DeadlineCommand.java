package locky.commands;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import locky.error.LockyException;
import locky.tasks.TaskList;
import locky.utils.Parser;

/**
 * Represents the {@code deadline} command.
 * When executed, it adds a new Deadline task with the given description
 * and due date/time into the TaskList.
 */
public class DeadlineCommand implements Command {
    private final String args;
    public DeadlineCommand(String args) {
        this.args = args;
    }

    @Override
    public String execute(TaskList list) throws LockyException, IOException {
        Parser.ParsedDeadline pd;
        try {
            pd = Parser.parseDeadlineArgs(args);
        } catch (Exception e) {
            if (e instanceof DateTimeParseException) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            }
            throw e;
        }
        list.addDeadline(pd.description(), pd.by());
        return "Added: " + list.getTask(list.getSize()) + "\n";
    }
}
