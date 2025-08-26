import java.util.Objects;
import java.util.Scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Locky {
    private static final Pattern DEADLINE_RE = Pattern.compile("^(.+?)\\s*/by\\s+(.+)$");
    private static final Pattern EVENT_RE    = Pattern.compile("^(.+?)\\s*/from\\s+(.+?)\\s*/to\\s+(.+)$");

    private final TaskList list;
    private final Scanner scanner;

    public Locky(String filePath) {
        Storage storage = new Storage(filePath);
        this.list = new TaskList(storage);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        String divider = "____________________________________________________________\n";
        String logo = "     __________\n" +
                "    / .------. \\\n" +
                "   / /        \\ \\\n" +
                "  _| |________| |_\n" +
                ".' |_|        |_| '.\n" +
                "'._____ ____ _____.'\n" +
                "|     .'    '.     |\n" +
                "'.  .'.      .'.  .'\n" +
                "'       LOCKY      '\n" +
                "|   '. .    . .'   |\n" +
                "'.________________.'\n";
        String intro = divider
        + "Hello! I'm Locky\n"
        + "Reminding you to Lock In!\n"
        + "What can I do for you?\n";

        System.out.println(logo + intro);

        // program is not being exited
        while (true) {
            String taskString = scanner.nextLine();

            if (Objects.equals(taskString, "bye")) {
                // exit
                System.out.println(divider + "You better Lock In!\n" + divider);
                break;
            }

            System.out.println(divider);

            try {
                handleLine(taskString);
            } catch (LockyException e) {
                // print validation error but keep program running
                System.out.println(e.getMessage() + "\n");
            } catch (Exception e) {
                // guard against unexpected runtime errors
                System.out.println("Unexpected error: " + e.getMessage() + "\n");
            }

        }

        // leave the program
        scanner.close();
    }

    private void handleLine(String taskString) throws LockyException {
        String trimmed = taskString == null ? "" : taskString.trim();
        if (trimmed.isEmpty()) throw new LockyException("Say something? (try: todo … / deadline … / event …)");

        String[] split = trimmed.split("\\s+", 2);
        String cmd  = split[0].toLowerCase();
        String args = split.length > 1 ? split[1].trim() : "";

        switch (cmd) {
            case "list": {
                System.out.println(list.printableList());
                break;
            }
            case "delete":
            case "mark":
            case "unmark": {
                if (args.isEmpty()) throw new LockyException("Which task number to " + cmd + "? e.g., \"" + cmd + " 2\"");
                int taskNumber;
                try {
                    taskNumber = Integer.parseInt(args);
                } catch (NumberFormatException e) {
                    throw new LockyException("Not a number: \"" + args + "\". Try \"" + cmd + " 2\".");
                }
                try {
                    Task t;
                    if (cmd.equals("mark")) {
                        boolean wasDone = list.get(taskNumber).getDone();
                        t = list.mark(taskNumber);
                        System.out.println((wasDone ? "You locked in once you don't have to do this again" :
                                "Locked In! Task marked as completed:"));
                    } else if (cmd.equals("unmark")) {
                        boolean wasDone = list.get(taskNumber).getDone();
                        t = list.unmark(taskNumber);
                        System.out.println((!wasDone ? "Oh.... it's still not done." :
                                "Ok, undone. Back to work!"));
                    } else {
                        t = list.delete(taskNumber);
                        System.out.println("Ok, so let's just forget that task existed...");
                    }
                    System.out.println(t + "\n");
                } catch (java.io.IOException ioe) {
                    System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
                }
                break;
            }
            case "todo": {
                if (args.isEmpty()) throw new LockyException("Todo needs a description. Try: \"todo buy milk\"");
                try {
                    list.addTodo(args);
                    System.out.println("Added: " + list.get(list.size()) + "\n");
                } catch (java.io.IOException ioe) {
                    System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
                }
                break;
            }
            case "deadline": {
                if (args.isEmpty()) throw new LockyException("Deadline needs \"description /by when\". Try: \"deadline CS2100 lab /by Mon 2359\"");
                Matcher m = DEADLINE_RE.matcher(args);
                if (!m.matches()) {
                    if (!args.contains("/by")) throw new LockyException("Missing \"/by\". Format: \"deadline <desc> /by <when>\"");
                    throw new LockyException("Bad deadline format. Use: \"deadline <desc> /by <when>\"");
                }
                String desc = m.group(1).trim();
                String by   = m.group(2).trim();
                if (desc.isEmpty()) throw new LockyException("Deadline description cannot be empty.");
                if (by.isEmpty())   throw new LockyException("Deadline time cannot be empty after /by.");

                try {
                    list.addDeadline(desc, by);
                    System.out.println("Added: " + list.get(list.size()) + "\n");
                } catch (java.io.IOException ioe) {
                    System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
                }
                break;
            }
            case "event": {
                if (args.isEmpty()) throw new LockyException("Event needs \"description /from start /to end\".");
                Matcher m = EVENT_RE.matcher(args);
                if (!m.matches()) {
                    if (!args.contains("/from")) throw new LockyException("Missing \"/from\". Format: \"event <desc> /from <start> /to <end>\"");
                    if (!args.contains("/to"))   throw new LockyException("Missing \"/to\". Format: \"event <desc> /from <start> /to <end>\"");
                    throw new LockyException("Bad event format. Use: \"event <desc> /from <start> /to <end>\"");
                }
                String desc  = m.group(1).trim();
                String start = m.group(2).trim();
                String end   = m.group(3).trim();
                if (desc.isEmpty())  throw new LockyException("Event description cannot be empty.");
                if (start.isEmpty()) throw new LockyException("Event start time cannot be empty after /from.");
                if (end.isEmpty())   throw new LockyException("Event end time cannot be empty after /to.");

                try {
                    list.addEvent(desc, start, end);
                    System.out.println("Added: " + list.get(list.size()) + "\n");
                } catch (java.io.IOException ioe) {
                    System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
                }
                break;
            }
            default:
                throw new LockyException("Unknown command. Try: list | todo | deadline | event | mark | unmark");
        }
    }

    public static void main(String[] args) {
        new Locky("./data/locky.txt").run();
    }
}
