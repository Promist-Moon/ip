import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;

public class Storage {
    private final File file;

    public Storage(String path) {
        this.file = new File(path);
    }

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

    public void save(ArrayList<Task> list) throws IOException {
        ensureParentDir();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (Task t : list) {
                bw.write(serialize(t));
                bw.newLine();
            }
        }
    }

    private void ensureParentDir() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            Files.createDirectories(Path.of(parent.toURI()));
        }
    }

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
                    d.getDeadline());
        } else if (t instanceof Event e) {
            return String.join("|",
                    "E",
                    e.getDone() ? "1" : "0",
                    e.getDescription(),
                    e.getStart(),
                    e.getEnd());
        }
        return "";
    }

    private Task parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 3) return null;

        String type = p[0];
        boolean done = "1".equals(p[1]);
        String desc = p[2];

        switch (type) {
            case "T":
                return new Todo(desc, done);
            case "D":
                return new Deadline(desc, done, p[3]);
            case "E":
                return new Event(desc, done, p[3], p[4]);
            default:
                return null;
        }
    }
}
