package locky.commands;

import java.io.IOException;

import locky.error.LockyException;
import locky.tasks.TaskList;

/**
 * Represents the {@code delete} command.
 * When executed, it removes the task at the specified index from the TaskList
 * and saves the updated list.
 */
public class DeleteCommand implements Command {
    private final String indexArg;
    public DeleteCommand(String indexArg) {
        this.indexArg = indexArg;
    }

    @Override public String execute(TaskList list) throws LockyException, IOException {
        if (indexArg == null || indexArg.isBlank()) {
            throw new LockyException("Which task number to delete? e.g., \"delete 2\"");
        }
        int idx;
        try {
            idx = Integer.parseInt(indexArg);
        } catch (NumberFormatException e) {
            throw new LockyException("Not a number: \"" + indexArg + "\". Try \"delete 2\".");
        }
        return "Ok, so let's just forget that task existed...\n" + list.delete(idx) + "\n";
    }
}
