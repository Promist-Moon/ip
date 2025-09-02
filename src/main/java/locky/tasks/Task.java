package locky.tasks;

/**
 * Represents a generic task with a description and a completion status.
 * This class serves as a parent class for the other subclasses of
 * tasks: {@code Locky.tasks.Todo}, {@code Locky.tasks.Deadline}, and {@code Locky.tasks.Event}.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates new task object with description and completion
     * status.
     *
     * @param description text description of task.
     * @param isDone whether the task is marked as completed.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    public void setDone() {
        this.isDone = true;
    }

    public void setUndone() {
        this.isDone = false;
    }

    public boolean getDone() {
        return this.isDone;
    }

    public String getDescription() {
        return this.description;
    }
}
