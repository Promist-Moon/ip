public class Event extends Task {
    String start;
    String end;

    public Event(String description, boolean isDone, String start, String end) {
        super(description, isDone);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " from: " + this.start + " to: " + this.end;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }
}
