package locky.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import locky.commands.Command;
import locky.commands.DeadlineCommand;
import locky.commands.DeleteCommand;
import locky.commands.EventCommand;
import locky.commands.FindCommand;
import locky.commands.ListCommand;
import locky.commands.MarkCommand;
import locky.commands.TodoCommand;
import locky.commands.UnmarkCommand;
import locky.error.LockyException;

/**
 * Parses user input lines into structured commands and arguments.
 * The Locky.utils.Parser centralizes regex and datetime parsing for commands like
 * creating deadline and event tasks. It validates required markers e.g., /by,
 * /from, /to and converts date/time strings to a LocalDateTime
 * using the expected input format.
 */
public final class Parser {

    // regex for command arguments
    private static final Pattern DEADLINE_RE = Pattern.compile("^(.+?)\\s*/by\\s+(.+)$");
    private static final Pattern EVENT_RE = Pattern.compile("^(.+?)\\s*/from\\s+(.+?)\\s*/to\\s+(.+)$");

    /**
     * Parses a raw input line into a command and its argument string.
     * The command is lower-cased; the remainder (if any) is returned as {@code args}.
     *
     * @param raw the raw user input line.
     * @return a ParsedCommand containing the command and args.
     * @throws LockyException if the input is empty or only whitespace.
     */
    public static ParsedCommand parseCommandLine(String raw) throws LockyException {
        String s = Objects.requireNonNull(raw, "raw").trim();
        if (s.isEmpty()) {
            throw new LockyException("Say something? (try: todo … / deadline … / event …)");
        }

        String[] split = s.split("\\s+", 2);
        String cmd = split[0].toLowerCase();
        String args = split.length > 1 ? split[1].trim() : "";
        return new ParsedCommand(cmd, args);
    }

    /**
     * Parses {@code deadline} arguments in the form
     * desc /by yyyy-MM-dd HHmm into a structured deadline.
     * Validates the presence of /by, non-empty description/time, and
     * converts the time string to a LocalDateTime.
     *
     * @param args the raw argument string after the deadline command.
     * @return a ParsedDeadline with description and due date/time.
     * @throws LockyException if the format is invalid or the date cannot be parsed.
     */
    public static ParsedDeadline parseDeadlineArgs(String args) throws LockyException {
        if (args.isEmpty()) {
            throw new LockyException(
                    "Deadline needs \"description /by when\". Try: \"deadline CS2100 lab /by 2019-12-02 1800\"");
        }
        Matcher m = DEADLINE_RE.matcher(args);
        if (!m.matches()) {
            if (!args.contains("/by")) {
                throw new LockyException(
                        "Missing \"/by\". Format: \"deadline <desc> /by yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)\"");
            }
            throw new LockyException(
                    "Bad deadline format. Use: \"deadline <desc> /by yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)\"");
        }

        String desc = m.group(1).trim();
        String by = m.group(2).trim();
        if (desc.isEmpty()) {
            throw new LockyException("Deadline description cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new LockyException("Deadline time cannot be empty after /by.");
        }

        try {
            LocalDateTime dt = LocalDateTime.parse(by, DateTimeFormat.INPUT);
            return new ParsedDeadline(desc, dt);
        } catch (DateTimeParseException dpe) {
            throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
        }
    }

    /**
     * Parses {@code event} arguments in the form
     * /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
     * into a structured event.
     * Validates the presence of /from and /to, ensures both times parse,
     * and checks that the end is strictly after the start.
     *
     * @param args the raw argument string after the event command.
     * @return a ParsedEvent with description, start, and end date/time.
     * @throws LockyException if the format is invalid, times cannot be parsed,
     *                        or the end is not after the start.
     */
    public static ParsedEvent parseEventArgs(String args) throws LockyException {
        if (args.isEmpty()) {
            throw new LockyException("Locky.tasks.Event needs \"description /from start /to end\".");
        }
        Matcher m = EVENT_RE.matcher(args);
        if (!m.matches()) {
            if (!args.contains("/from")) {
                throw new LockyException("Missing \"/from\". Format: \"event <desc> /from <start> /to <end>\"");
            }
            if (!args.contains("/to")) {
                throw new LockyException("Missing \"/to\".   Format: \"event <desc> /from <start> /to <end>\"");
            }
            throw new LockyException("Bad event format. Use: \"event <desc> /from <start> /to <end>\"");
        }

        String desc = m.group(1).trim();
        String start = m.group(2).trim();
        String end = m.group(3).trim();
        if (desc.isEmpty()) {
            throw new LockyException("Locky.tasks.Event description cannot be empty.");
        }
        if (start.isEmpty()) {
            throw new LockyException("Locky.tasks.Event start time cannot be empty after /from.");
        }
        if (end.isEmpty()) {
            throw new LockyException("Locky.tasks.Event end time cannot be empty after /to.");
        }

        try {
            LocalDateTime startDt = LocalDateTime.parse(start, DateTimeFormat.INPUT);
            LocalDateTime endDt = LocalDateTime.parse(end, DateTimeFormat.INPUT);
            if (!endDt.isAfter(startDt)) {
                throw new LockyException("Locky.tasks.Event end must be after start.");
            }
            return new ParsedEvent(desc, startDt, endDt);
        } catch (DateTimeParseException dpe) {
            throw new LockyException("Invalid date format. Use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
        }
    }


    /**
     * Parses a raw line into a specific Command.
     *
     * @param raw the raw user input line
     * @return a Command ready to be executed
     * @throws LockyException if the input is invalid or unknown
     */
    public static Command parse(String raw) throws LockyException {
        ParsedCommand pc = parseCommandLine(raw);
        String cmd = pc.command();
        String args = pc.args();

        switch (cmd) {
        case "list":
            return new ListCommand();
        case "todo":
            return new TodoCommand(args);
        case "deadline":
            return new DeadlineCommand(args);
        case "event":
            return new EventCommand(args);
        case "mark":
            return new MarkCommand(args);
        case "unmark":
            return new UnmarkCommand(args);
        case "delete":
            return new DeleteCommand(args);
        case "find":
            return new FindCommand(args);
        default:
            throw new LockyException("Unknown command. Try: list | todo | deadline | event | mark | unmark | find");
        }
    }

    /**
     * Holds a parsed command and its raw arguments
     *
     * @param command
     * @param args
     */
    public record ParsedCommand(String command, String args) {}

    /**
     * Holds a parsed deadline description and due date/time
     *
     * @param description
     * @param by
     */
    public record ParsedDeadline(String description, LocalDateTime by) {}

    /**
     * Holds a parsed event description, and start/end date/time
     *
     * @param description
     * @param start
     * @param end
     */
    public record ParsedEvent(String description, LocalDateTime start, LocalDateTime end) {}
}
