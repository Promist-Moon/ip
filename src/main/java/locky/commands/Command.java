package locky.commands;

import java.io.IOException;

import locky.error.LockyException;
import locky.tasks.TaskList;

/**
 * Represents a user command that can be executed on a {@link locky.tasks.TaskList}.
 */
public interface Command {
    /**
     * Executes this command on the given {@link locky.tasks.TaskList}.
     *
     * @param list the TaskList on which to execute the command.
     * @return a formatted string message describing the result of execution.
     * @throws locky.error.LockyException if the command arguments are invalid
     * @throws java.io.IOException if persisting the updated TaskList to storage fails.
     */
    String execute(TaskList list) throws LockyException, IOException;
}
