package locky.commands;

import java.io.IOException;

import locky.error.LockyException;
import locky.tasks.TaskList;

/**
 * Represents the {@code todo} command.
 * When executed, it adds a new Todo task with the given description
 * into the TaskList.
 */
public class TodoCommand implements Command {
    private final String description;
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public String execute(TaskList list) throws LockyException, IOException {
        if (description == null || description.isBlank()) {
            throw new LockyException("Todo needs a description. Try: \"todo buy milk\"");
        }
        list.addTodo(description);
        return "Added: " + list.getTask(list.getSize()) + "\n";
    }
}
