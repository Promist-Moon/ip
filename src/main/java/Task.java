public class Task {
    String description;
    boolean isDone;

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
