package locky.utils;

import locky.tasks.Deadline;
import locky.tasks.Event;
import locky.tasks.Task;
import locky.tasks.Todo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent storage of tasks to and from a text file.
 */
public class Storage {
    private static final List<DateTimeFormatter> ACCEPTED_INPUT_FORMATS = List.of(
            DateTimeFormat.INPUT,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormat.DISPLAY
    );
    private final File file;

    /**
     * Creates a new {@code Locky.utils.Storage} instance that will read from and
     * write to the given file path.
     *
     * @param path path to the file used for persistent storage.
     */
    public Storage(String path) {
        this.file = new File(path);
    }

    /**
     * Loads tasks from the storage file into memory.
     * Each line in the file is parsed into a corresponding {@code Locky.tasks.Task}
     * object.
     * If the file does not exist, an empty list is returned.
     *
     * @return a list of tasks read from the storage file.
     * @throws IOException IOException if an I/O error occurs while reading the file.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> list = new ArrayList<>();
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Task t = parseLine(line);
                if (t != null) list.add(t);
            }
        }
        return list;
    }

    /**
     * Saves all tasks to the storage file.
     * Tasks are serialized into pipe-delimited strings and written
     * line by line, overwriting any existing file contents.
     *
     * @param list the list of tasks to be written to storage.
     * @throws IOException if an I/O error occurs while writing the file.
     */
    public void save(ArrayList<Task> list) throws IOException {
        ensureParentDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Task t : list) {
                bw.write(serialize(t));
                bw.newLine();
            }
        }
    }

    /**
     * Ensures that the parent directory of the storage file exists.
     * If the directory does not exist, it will be created.
     *
     * @throws IOException if the directories cannot be created.
     */
    private void ensureParentDir() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            Files.createDirectories(Path.of(parent.toURI()));
        }
    }

    /**
     * Converts a {@code Locky.tasks.Task} object into its string representation
     * suitable for saving to disk.
     *
     * @param t the task to be serialized.
     * @return the serialized form of the task.
     */
    private String serialize(Task t) {
        if (t instanceof Todo todo) {
            return String.join("|",
                    "T",
                    todo.getDone() ? "1" : "0",
                    todo.getDescription());
        } else if (t instanceof Deadline d) {
            return String.join("|",
                    "D",
                    d.getDone() ? "1" : "0",
                    d.getDescription(),
                    d.getDeadlineFormatted());
        } else if (t instanceof Event e) {
            return String.join("|",
                    "E",
                    e.getDone() ? "1" : "0",
                    e.getDescription(),
                    e.getStartFormatted(),
                    e.getEndFormatted());
        }
        return "";
    }

    /**
     * Parses a serialized line from the storage file back into a
     * corresponding {@code Locky.tasks.Task} object.
     *
     * @param line the line of text to be parsed.
     * @return the reconstructed task, or null if the line
     *         is malformed or unrecognized.
     */
    private Task parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 3) return null;

        String type = p[0];
        boolean done = "1".equals(p[1]);
        String desc = p[2];

        switch (type) {
            case "T":
                return new Todo(desc, done);

            case "D": {
                if (p.length < 4) return null;
                LocalDateTime by = parseDateFlexible(p[3]);
                return new Deadline(desc, done, by);
            }

            case "E": {
                if (p.length < 5) return null;
                LocalDateTime start = parseDateFlexible(p[3]);
                LocalDateTime end   = parseDateFlexible(p[4]);
                return new Event(desc, done, start, end);
            }

            default:
                return null;
        }
    }


    /**
     * Attempts to parse a string into a LocalDateTime using a list
     * of accepted date/time formats.
     * This method tries each accepted formatter in sequence until one
     * succeeds. If none match, an exception is thrown.
     *
     * @param raw the string to be parsed into a date/time.
     * @return the parsed LocalDateTime.
     * @throws IllegalArgumentException if the string cannot be parsed by
     *                                  any accepted format.
     */
    private LocalDateTime parseDateFlexible(String raw) {
        for (DateTimeFormatter fmt : ACCEPTED_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(raw, fmt);
            } catch (DateTimeParseException ignore) {
                // try next
            }
        }
        throw new IllegalArgumentException("Unrecognized datetime: \"" + raw
                + "\". Expected formats like \"" + DateTimeFormat.INPUT.toString() + "\" or ISO-8601.");
    }
}
