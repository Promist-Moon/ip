public class Deadline extends Task {
    String deadline;

    public Deadline(String description, boolean isDone, String deadline) {
        super(description, isDone);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " by: " + this.deadline;
    }

    public String getDeadline() {
        return this.deadline;
    }
}
