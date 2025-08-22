import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.*;

public class Locky {
    public static void main(String[] args) {
        String divider = "____________________________________________________________\n";
        String logo = "     __________ \n" +
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

        ArrayList<Task> list = new ArrayList<>();

        System.out.println(logo + intro);
        Scanner scanner = new Scanner(System.in);
        String taskString = scanner.nextLine();

        // program is not being exited
        while (!Objects.equals(taskString, "bye")) {
            System.out.println(divider);

            if (Objects.equals(taskString, "list")) {
                // list out tasks
                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i).toString());
                }
                System.out.println("\n");
                taskString = scanner.nextLine();
                continue;
            } else if (taskString.startsWith("mark ") || taskString.startsWith("unmark ")) {
                // mark done
                String[] parts = taskString.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                int index = taskNumber - 1;

                // set message
                if (taskString.startsWith("mark")) {
                    if (list.get(index).getDone()) {
                        System.out.println("Already done!");
                    } else {
                        list.get(index).setDone();
                        System.out.println("Locked In! Task marked as completed:");
                    }
                } else {
                    if (!list.get(index).getDone()) {
                        System.out.println("Oh.... it's still not done.");
                    } else {
                        list.get(index).setDone();
                        System.out.println("Oh... you're useless.");
                    }
                }
                System.out.println(list.get(index).toString() + "\n");
                taskString = scanner.nextLine();
                continue;
            }

            if (taskString.startsWith("todo")) {
                String description = taskString.substring(5).trim();
                Todo task = new Todo(description, false);
                list.add(task);
                System.out.println("Added: " + task);
            } else if (taskString.startsWith("deadline")) {
                Pattern p = Pattern.compile("deadline\\s+(.*?)\\s*/by\\s+(.*)");
                Matcher m = p.matcher(taskString);

                if (m.matches()) {
                    String desc = m.group(1);
                    String deadline = m.group(2);
                    Deadline task = new Deadline(desc, false, deadline);
                    list.add(task);
                    System.out.println("Added: " + task);
                }
            } else if (taskString.startsWith("event")) {
                Pattern p = Pattern.compile("event\\s+(.*?)\\s*/from\\s+(.*?)\\s*/to\\s+(.*)");
                Matcher m = p.matcher(taskString);

                if (m.matches()) {
                    String description = m.group(1);
                    String start = m.group(2);
                    String end = m.group(3);
                    Event task = new Event(description, false, start, end);
                    list.add(task);
                    System.out.println("Added: " + task);
                }
            }

            // print task added
            taskString = scanner.nextLine();
        }

        // leave the program
        System.out.println(divider
                + "You better Lock In!\n"
                + divider);
        scanner.close();
    }
}
