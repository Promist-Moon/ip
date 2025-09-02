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

    /**
     * Handles a single line of user input.
     * Delegates parsing to Locky.utils.Parser, and depending on the command
     * manipulates the task list accordingly (add, delete, mark, unmark, etc.).
     * Prints output for the user.
     *
     * @param taskString the raw user input line.
     * @throws LockyException if the command or its arguments are invalid.
     */
    private void handleLine(String taskString) throws LockyException {
        Parser.ParsedCommand pc = Parser.parseCommandLine(taskString);
        String cmd = pc.command();
        String args = pc.args();

        switch (cmd) {
        case "list": {
            System.out.println(list.printList());
            break;
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
                if (cmd.equals("mark")) {
                    boolean wasDone = list.getTask(taskNumber).getDone();
                    t = list.mark(taskNumber);
                    System.out.println((wasDone ? "You locked in once you don't have to do this again"
                            : "Locked In! Task marked as completed:"));
                } else if (cmd.equals("unmark")) {
                    boolean wasDone = list.getTask(taskNumber).getDone();
                    t = list.unmark(taskNumber);
                    System.out.println((!wasDone ? "Oh.... it's still not done."
                            : "Ok, undone. Back to work!"));
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
            if (args.isEmpty()) {
                throw new LockyException("Todo needs a description. Try: \"todo buy milk\"");
            }
            try {
                list.addTodo(args);
                System.out.println("Added: " + list.getTask(list.getSize()) + "\n");
            } catch (java.io.IOException ioe) {
                System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
            }
            break;
        }
        case "deadline": {
            Parser.ParsedDeadline pd = Parser.parseDeadlineArgs(args);

            try {
                list.addDeadline(pd.description(), pd.by());
                System.out.println("Added: " + list.getTask(list.getSize()) + "\n");
            } catch (DateTimeParseException dpe) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            } catch (java.io.IOException ioe) {
                System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
            }
            break;
        }
        case "event": {
            Parser.ParsedEvent pe = Parser.parseEventArgs(args);
            try {
                list.addEvent(pe.description(), pe.start(), pe.end());
                System.out.println("Added: " + list.getTask(list.getSize()) + "\n");
            } catch (DateTimeParseException dpe) {
                throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
            } catch (java.io.IOException ioe) {
                System.out.println("(Warning: failed to save: " + ioe.getMessage() + ")\n");
            }
            break;
        }
        case "find": {
            if (args.isEmpty()) {
                throw new LockyException("Find needs a description. Try: \"find milk\"");
            }
            try {
                ArrayList<Task> matches = list.find(args);
                if (matches.isEmpty()) {
                    System.out.println("No matching tasks found.");
                } else {
                    System.out.println("Matching tasks:");
                    for (int i = 0; i < matches.size(); i++) {
                        System.out.println((i + 1) + ". " + matches.get(i));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while finding tasks: " + e.getMessage());
            }
            break;
        }
        default:
            throw new LockyException("Unknown command. Try: list | todo | deadline | event | mark | unmark | find");
        }
    }

    /**
     * Entry point of the application.
     * <p>
     * Creates a {@code Locky.app.Locky} instance with {@code ./data/locky.txt}
     * as the storage file and starts the chatbot loop.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Locky("./data/locky.txt").run();
    }
}
