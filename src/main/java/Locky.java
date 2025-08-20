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

        ArrayList<String> list = new ArrayList<>();

        System.out.println(logo + intro);
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while (!Objects.equals(command, "bye")) {
            System.out.println(divider);
            if (Objects.equals(command, "list")) {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i));
                }
                System.out.println("\n");
                command = scanner.nextLine();
                continue;
            }
            System.out.println("added: "
                    + command
                    + "\n");
            list.add(command);
            command = scanner.nextLine();
        }
        System.out.println(divider
                + "You better Lock In!\n"
                + divider);
        scanner.close();
    }
}
