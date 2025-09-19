package locky.commands;

import locky.tasks.TaskList;

/**
 * Represents the {@code list} command.
 * When executed, it prints all tasks currently in the TaskList
 * in a numbered, formatted list.
 */
public class ListCommand implements Command {
    @Override
    public String execute(TaskList list) {
        String message;
        if (list.isEmpty()) {
            message = "Looky looky your Locky task list is empty! Time to get started!\n";
        } else {
            message = "Oh my, look at all these tasks! Chop chop!\n";
        }
        return message + list.getListString();
    }
}
