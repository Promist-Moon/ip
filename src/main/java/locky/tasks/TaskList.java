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
        ArrayList<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            System.out.println("(Could not load previous tasks: " + e.getMessage() + ")");
            loadedTasks = new ArrayList<>();
        }
        this.tasks = loadedTasks;
    }

    private TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
        this.storage = null;
    }

    public int getSize() {
        return tasks.size();
    }

    public Task getTask(int indexOneBased) throws LockyException {
        int idx = indexOneBased - 1;
        if (idx < 0 || idx >= tasks.size()) {
            throw new LockyException("No such task: " + indexOneBased);
        }
        return tasks.get(idx);
    }

    public boolean isTaskDone(int indexOneBased) throws LockyException {
        return this.getTask(indexOneBased).getDone();
    }

    public boolean isEmpty() {
        return this.getSize() == 0;
    }

    /**
     * Returns a string representation of the task list, with
     * each task prefixed by its index.
     *
     * @return the formatted string representation of the task list.
     */
    public String getListString() {
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
    public void addEvent(String desc, LocalDateTime from, LocalDateTime to) throws IOException, LockyException {
        Event clashing = findClash(from, to);
        if (clashing != null) {
            throw new LockyException("Clash with existing event: "
                    + clashing.getDescription() + " ("
                    + clashing.getFormattedStart() + "–" + clashing.getFormattedEnd() + ")");
        }
        tasks.add(new Event(desc, false, from, to));
        save();
    }

    /**
     * Finds clash in timings for existing events when a new Event
     * is added into tasks.
     *
     * @param from the LocalDateTime of the proposed start of Event.
     * @param to the LocalDateTime of the proposed end of Event.
     * @return null if no clashes; Event it clashes with.
     */
    private Event findClash(LocalDateTime from, LocalDateTime to) {
        for (Task t : this.tasks) {
            if (!(t instanceof Event)) {
                continue;
            }
            Event e = (Event) t;
            if (e.isClashing(from, to)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Marks the task at the given index as completed and saves the updated list.
     *
     * @param indexOneBased the 1-based index of the task to mark.
     * @return the updated task that was marked as completed.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task mark(int indexOneBased) throws IOException, LockyException {
        Task t = getTask(indexOneBased);
        t.setDone();
        save();
        return t;
    }

    /**
     * Marks the task at the given index as not completed and saves the updated list.
     *
     * @param indexOneBased the 1-based index of the task to unmark.
     * @return the updated task that was unmarked.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task unmark(int indexOneBased) throws IOException, LockyException {
        Task t = getTask(indexOneBased);
        t.setUndone();
        save();
        return t;
    }

    /**
     * Deletes the task at the given index and saves the updated list.
     *
     * @param indexOneBased the 1-based index of the task to delete.
     * @return the task that was removed from the list.
     * @throws IOException if saving the updated list fails.
     * @throws LockyException if the index is invalid.
     */
    public Task delete(int indexOneBased) throws IOException, LockyException {
        Task t = getTask(indexOneBased);
        tasks.remove(indexOneBased - 1);
        save();
        return t;
    }

    /**
     * Finds a list of tasks with descriptions
     * matching the given keyword.
     *
     * @param keyword String matcher.
     * @return ArrayList of tasks containing keyword in description.
     */
    private TaskList find(String keyword) {
        ArrayList<Task> resultsArray = new ArrayList<>();
        String key = keyword.trim().toLowerCase();

        // iterate through each task to find matching descriptions
        for (Task t : tasks) {
            assert t != null : "Task must not be null";
            assert t.getDescription() != null : "Task description must not be null";
            if (t != null && t.isMatching(key)) {
                resultsArray.add(t);
            }
        }
        return new TaskList(resultsArray);
    }

    /**
     * Formats a list of tasks into a string
     * of search results.
     *
     * @param keyword String matcher.
     * @return String of tasks containing keyword in description.
     */
    public String formatFindResults(String keyword) {
        TaskList matches = find(keyword);
        if (matches.isEmpty()) {
            return "No matching tasks found.\n";
        } else {
            return matches.getListString();
        }
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
