package locky.commands;

import java.io.IOException;

import locky.error.LockyException;
import locky.tasks.Task;
import locky.tasks.TaskList;

/**
 * Represents the {@code mark} command.
 * When executed, it marks the task at the specified index in the TaskList
 * as completed, and saves the updated list.
 */
public class MarkCommand implements Command {
    private final String indexArg;
    public MarkCommand(String indexArg) { this.indexArg = indexArg; }

    @Override public String execute(TaskList list) throws LockyException, IOException {
        if (indexArg == null || indexArg.isBlank()) {
            throw new LockyException("Which task number to mark? e.g., \"mark 2\"");
        }
        int idx;
        try {
            idx = Integer.parseInt(indexArg);
        } catch (NumberFormatException e) {
            throw new LockyException("Not a number: \"" + indexArg + "\". Try \"mark 2\".");
        }
        boolean wasDone = list.isTaskDone(idx);
        Task t = list.mark(idx);
        String msg = wasDone
                ? "You locked in once you don't have to do this again"
                : "Locked In! Task marked as completed:";
        return msg + "\n" + t + "\n";
    }
}
