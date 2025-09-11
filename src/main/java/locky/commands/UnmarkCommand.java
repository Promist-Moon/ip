package locky.commands;

import java.io.IOException;

import locky.error.LockyException;
import locky.tasks.Task;
import locky.tasks.TaskList;

/**
 * Represents the {@code unmark} command.
 * When executed, it marks the task at the specified index in the TaskList
 * as not completed, and saves the updated list.
 */
public class UnmarkCommand implements Command {
    private final String indexArg;
    public UnmarkCommand(String indexArg) {
        this.indexArg = indexArg;
    }

    @Override public String execute(TaskList list) throws LockyException, IOException {
        if (indexArg == null || indexArg.isBlank()) {
            throw new LockyException("Which task number to unmark? e.g., \"unmark 2\"");
        }
        int idx;
        try {
            idx = Integer.parseInt(indexArg);
        } catch (NumberFormatException e) {
            throw new LockyException("Not a number: \"" + indexArg + "\". Try \"unmark 2\".");
        }
        boolean wasDone = list.isTaskDone(idx);
        Task t = list.unmark(idx);
        String msg = !wasDone
                ? "Oh.... it's still not done."
                : "Ok, undone. Back to work!";
        return msg + "\n" + t + "\n";
    }
}
