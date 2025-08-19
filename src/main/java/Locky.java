import java.util.Objects;
import java.util.Scanner;

public class Locky {
    public static void main(String[] args) {
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
        String intro = "____________________________________________________________\n"
        + "Hello! I'm Locky\n"
        + "Reminding you to Lock In!\n"
        + "What can I do for you?\n";
        System.out.println(logo + intro);
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        while (!Objects.equals(command, "bye")) {
            System.out.println("____________________________________________________________\n"
                    + command
                    + "\n");
            command = scanner.nextLine();
        }
        System.out.println("____________________________________________________________\n"
                + "You better Lock In!\n"
                + "____________________________________________________________");
        scanner.close();
    }
}
