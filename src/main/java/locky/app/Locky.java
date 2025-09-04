package locky.app;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import locky.error.LockyException;
import locky.tasks.Task;
import locky.tasks.TaskList;
import locky.utils.Parser;
import locky.utils.Storage;

/**
 * Main entry point and controller for the Locky chatbot.
 */
public class Locky {
    private final TaskList list;
    private final Scanner scanner;

    /**
     * Creates a new Locky.app.Locky instance using the specified file path
     * for persistent storage of tasks.
     *
     * @param filePath the file path where tasks are stored and loaded.
     */
    public Locky(String filePath) {
        Storage storage = new Storage(filePath);
        this.list = new TaskList(storage);
        this.scanner = new Scanner(System.in);
    }

    public String getGreeting() {
        String intro = "Hello! I'm Locky\n"
                + "Reminding you to Lock In!\n"
                + "What can I do for you?\n";
        return intro;
    }

    public String getResponse(String input) {
        if (Objects.equals(input, "bye")) {
            return "You better Lock In!\n";
        }

        try {
            return handleLineToString(input);
        } catch (LockyException e) {
            return e.getMessage() + "\n";
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage() + "\n";
        }
    }

    /**
     * Starts the interactive chatbot loop.
     * Prints  welcome message and continues reading user input until
     * the bye command is received. Each line is parsed and handled
     * as a task-related command.
     */
    public void run() {
        String divider = "____________________________________________________________\n";
        String logo = "     __________\n"
                + "    / .------. \\\n"
                + "   / /        \\ \\\n"
                + "  _| |________| |_\n"
                + ".' |_|        |_| '.\n"
                + "'._____ ____ _____.'\n"
                + "|     .'    '.     |\n"
                + "'.  .'.      .'.  .'\n"
                + "'       LOCKY      '\n"
                + "|   '. .    . .'   |\n"
                + "'.________________.'\n";
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
            System.out.print(getResponse(taskString));
        }

        // leave the program
        scanner.close();
    }

    private String handleLineToString(String taskString) throws LockyException {
        Parser.ParsedCommand pc = Parser.parseCommandLine(taskString);
        String cmd = pc.command();
        String args = pc.args();

        switch (cmd) {
        case "list": {
            return list.printList();
        }
        case "delete":
        case "mark":
        case "unmark": {
            if (args.isEmpty()) {
                throw new LockyException("Which task number to " + cmd + "? e.g., \"" + cmd + " 2\"");
            }
            int taskNumber;
            try {
                taskNumber = Integer.parseInt(args);
            } catch (NumberFormatException e) {
                throw new LockyException("Not a number: \"" + args + "\". Try \"" + cmd + " 2\".");
            }
            try {
                Task t;
                String prefix;
                if (cmd.equals("mark")) {
                    boolean wasDone = list.getTask(taskNumber).getDone();
                    t = list.mark(taskNumber);
                    prefix = (wasDone ? "You locked in once you don't have to do this again"
                            : "Locked In! Task marked as completed:");
                } else if (cmd.equals("unmark")) {
                    boolean wasDone = list.getTask(taskNumber).getDone();
                    t = list.unmark(taskNumber);
                    prefix = (!wasDone ? "Oh.... it's still not done."
                            : "Ok, undone. Back to work!");
                } else {
                    t = list.delete(taskNumber);
                    prefix = "Ok, so let's just forget that task existed...";
                }
                return prefix + "\n" + t + "\n";
            } catch (java.io.IOException ioe) {
                return "(Warning: failed to save: " + ioe.getMessage() + ")\n";
            }
        }
        case "todo": {
            if (args.isEmpty()) {
                throw new LockyException("Todo needs a description. Try: \"todo buy milk\"");
            }
            try {
                list.addTodo(args);
                return "Added: " + list.getTask(list.getSize()) + "\n";
            } catch (java.io.IOException ioe) {
                return "(Warning: failed to save: " + ioe.getMessage() + ")\n";
            }
        }
        case "deadline": {
            Parser.ParsedDeadline pd = Parser.parseDeadlineArgs(args);
            try {
                list.addDeadline(pd.description(), pd.by());
                return "Added: " + list.getTask(list.getSize()) + "\n";
            } catch (DateTimeParseException dpe) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            } catch (java.io.IOException ioe) {
                return "(Warning: failed to save: " + ioe.getMessage() + ")\n";
            }
        }
        case "event": {
            Parser.ParsedEvent pe = Parser.parseEventArgs(args);
            try {
                list.addEvent(pe.description(), pe.start(), pe.end());
                return "Added: " + list.getTask(list.getSize()) + "\n";
            } catch (DateTimeParseException dpe) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            } catch (java.io.IOException ioe) {
                return "(Warning: failed to save: " + ioe.getMessage() + ")\n";
            }
        }
        case "find": {
            if (args.isEmpty()) {
                throw new LockyException("Find needs a description. Try: \"find milk\"");
            }
            try {
                ArrayList<Task> matches = list.find(args);
                if (matches.isEmpty()) {
                    return "No matching tasks found.\n";
                } else {
                    StringBuilder sb = new StringBuilder("Matching tasks:");
                    for (int i = 0; i < matches.size(); i++) {
                        sb.append("\n").append(i + 1).append(". ").append(matches.get(i));
                    }
                    sb.append("\n");
                    return sb.toString();
                }
            } catch (Exception e) {
                return "Error while finding tasks: " + e.getMessage() + "\n";
            }
        }
        default:
            throw new LockyException("Unknown command. Try: list | todo | deadline | event | mark | unmark | find");
        }
    }
}
