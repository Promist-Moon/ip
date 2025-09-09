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
        return list.printList();
    }
}
