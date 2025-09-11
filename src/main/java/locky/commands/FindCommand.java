package locky.commands;

import locky.error.LockyException;
import locky.tasks.TaskList;

/**
 * Represents the {@code find} command.
 * When executed, it searches the TaskList for tasks whose descriptions
 * contain the specified keyword and returns a
 * formatted list of matches.
 */
public class FindCommand implements Command {
    private final String keyword;
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList list) throws LockyException {
        if (keyword == null || keyword.isBlank()) {
            throw new LockyException("Find needs a description. Try: \"find milk\"");
        }
        return list.formatFindResults(keyword);
    }
}
