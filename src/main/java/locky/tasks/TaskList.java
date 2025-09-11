package locky.tasks;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import locky.error.LockyException;
import locky.utils.Storage;

/**
 * Represents an in-memory list of tasks that can be modified and persisted.
 * The {@code Locky.tasks.TaskList} class manages a collection of tasks, including
 * {@code Locky.tasks.Todo}, {@code Locky.tasks.Deadline}, and {@code Locky.tasks.Event}. It supports adding,
 * retrieving, marking, unmarking, deleting, and printing tasks. Changes
 * are saved automatically to the associated {@link Storage} object.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final Storage storage;

    /**
     * Creates a new Locky.tasks.TaskList backed by the given storage.
     * Attempts to load existing tasks from the storage file. If the file
     * cannot be read, an empty task list is created instead.
     *
     * @param storage the Locky.utils.Storage object used to load and save tasks.
     */
    public TaskList(Storage storage) {
        this.storage = storage;
        ArrayList<Task> loaded;
        try {
            loaded = storage.load();
        } catch (IOException e) {
            System.out.println("(Could not load previous tasks: " + e.getMessage() + ")");
            loaded = new ArrayList<>();
        }
        this.tasks = loaded;
    }

    public int getSize() {
        return tasks.size();
    }

    public Task getTask(int index1Based) throws LockyException {
        int idx = index1Based - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new LockyException("No such task: " + index1Based);
        }
        return tasks.get(idx);
    }


    /**
     * Returns a string representation of the task list, with
     * each task prefixed by its index.
     *
     * @return the formatted string representation of the task list.
     */
    public String printList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Adds a new todo task to the list and saves the updated list.
     *
     * @param desc the description of the todo task.
     * @throws IOException if saving the updated list fails.
     */
    public void addTodo(String desc) throws IOException {
        tasks.add(new Todo(desc, false));
        save();
    }

    /**
     * Adds a new deadline task to the list and saves the updated list.
     *
     * @param desc the description of the deadline task.
     * @param deadline the due date and time of the task.
     * @throws IOException if saving the updated list fails.
     */
    public void addDeadline(String desc, LocalDateTime deadline) throws IOException {
        tasks.add(new Deadline(desc, false, deadline));
        save();
    }

    /**
     * Adds a new event task to the list and saves the updated list.
     *
     * @param desc the description of the event task.
     * @param from the start date and time of the event.
     * @param to the end date and time of the event.
     * @throws IOException if saving the updated list fails.
     */
    public void addEvent(String desc, LocalDateTime from, LocalDateTime to) throws IOException {
        tasks.add(new Event(desc, false, from, to));
        save();
    }

    /**
     * Marks the task at the given index as completed and saves the updated list.
     *
     * @param index1Based the 1-based index of the task to mark.
     * @return the updated task that was marked as completed.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task mark(int index1Based) throws IOException, LockyException {
        Task t = getTask(index1Based);
        t.setDone();
        save();
        return t;
    }

    /**
     * Marks the task at the given index as not completed and saves the updated list.
     *
     * @param index1Based the 1-based index of the task to unmark.
     * @return the updated task that was unmarked.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task unmark(int index1Based) throws IOException, LockyException {
        Task t = getTask(index1Based);
        t.setUndone();
        save();
        return t;
    }

    /**
     * Deletes the task at the given index and saves the updated list.
     *
     * @param index1Based the 1-based index of the task to delete.
     * @return the task that was removed from the list.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task delete(int index1Based) throws IOException, LockyException {
        Task t = getTask(index1Based);
        tasks.remove(index1Based - 1);
        save();
        return t;
    }

    /**
     * Finds a list of tasks with descriptions
     * matching the given keyword.
     *
     * @param keyword String matcher
     * @return ArrayList of tasks containing keyword in description
     */
    public ArrayList<Task> find(String keyword) {
        ArrayList<Task> results = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Task t : tasks) {
            if (t.getDescription() != null
                    && t.getDescription().toLowerCase().contains(key)) {
                results.add(t);
            }
        }
        return results;
    }

    /**
     * Saves the current task list to persistent storage.
     *
     * @throws IOException if an I/O error occurs during saving.
     */
    private void save() throws IOException {
        storage.save(tasks);
    }
}
