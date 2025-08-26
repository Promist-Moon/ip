import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;
    private final Storage storage;

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

    // ------- Read-only helpers -------
    public int size() { return tasks.size(); }
    public Task get(int index1Based) throws LockyException {
        int idx = index1Based - 1;
        if (idx < 0 || idx >= tasks.size()) throw new LockyException("No such task: " + index1Based);
        return tasks.get(idx);
    }
    public List<Task> asList() { return tasks; }

    public String printableList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString();
    }

    // ------- Mutations (auto-save) -------
    public void addTodo(String desc) throws IOException {
        tasks.add(new Todo(desc, false));
        save();
    }

    public void addDeadline(String desc, String by) throws IOException {
        tasks.add(new Deadline(desc, false, by));
        save();
    }

    public void addEvent(String desc, String from, String to) throws IOException {
        tasks.add(new Event(desc, false, from, to));
        save();
    }

    public Task mark(int index1Based) throws IOException, LockyException {
        Task t = get(index1Based);
        t.setDone();
        save();
        return t;
    }

    public Task unmark(int index1Based) throws IOException, LockyException {
        Task t = get(index1Based);
        t.setUndone();
        save();
        return t;
    }

    public Task delete(int index1Based) throws IOException, LockyException {
        Task t = get(index1Based);
        tasks.remove(index1Based - 1);
        save();
        return t;
    }

    private void save() throws IOException {
        storage.save(tasks);
    }
}
