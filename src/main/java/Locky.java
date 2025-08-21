import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;

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
        String command = scanner.nextLine();

        // program is not being exited
        while (!Objects.equals(command, "bye")) {
            System.out.println(divider);

            if (Objects.equals(command, "list")) {
                // list out tasks
                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i).toString());
                }
                System.out.println("\n");
                command = scanner.nextLine();
                continue;
            } else if (command.startsWith("mark ") || command.startsWith("unmark ")) {
                // mark done
                String[] parts = command.split(" ");
                int taskNumber = Integer.parseInt(parts[1]);
                int index = taskNumber - 1;
                list.get(index).setDone();

                // set message
                if (command.startsWith("mark")) {
                    System.out.println("Locked In! Task marked as completed:");
                } else {
                    System.out.println("Oh... you're useless.");
                }
                System.out.println(list.get(index).toString() + "\n");
                command = scanner.nextLine();
                continue;
            }

            // print task added
            System.out.println("added: "
                    + command
                    + "\n");
            list.add(new Task(command, false));
            command = scanner.nextLine();
        }

        // leave the program
        System.out.println(divider
                + "You better Lock In!\n"
                + divider);
        scanner.close();
    }
}
