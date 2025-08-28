package locky.utils;

import locky.error.LockyException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    @Test
    public void parseCommandLine_blankInput_throwsLockyException() {
        LockyException ex = assertThrows(
                LockyException.class,
                () -> Parser.parseCommandLine("   ")
        );
        assertTrue(ex.getMessage().contains("Say something?"),
                "Should prompt the user to type something");
    }

    @Test
    public void parseCommandLine_commandWithArgs_trimsOnlyOuterWhitespace() throws Exception {
        Parser.ParsedCommand pc = Parser.parseCommandLine("todo    buy   milk  ");
        assertEquals("todo", pc.command());
        // Leading/trailing trimmed, inner spacing preserved
        assertEquals("buy   milk", pc.args());
    }

    @Test
    public void parseCommandLine_validTodoCommand_success() throws Exception {
        Parser.ParsedCommand pc = Parser.parseCommandLine("todo buy milk");
        assertEquals("todo", pc.command());
        assertEquals("buy milk", pc.args());
    }

    @Test
    public void parseCommandLine_validDeadlineCommand_success() throws Exception {
        Parser.ParsedCommand pc = Parser.parseCommandLine(
                "deadline submit report /by 2025-08-30 1800");
        assertEquals("deadline", pc.command());
        assertEquals("submit report /by 2025-08-30 1800", pc.args());
    }


    @Test
    public void parseCommandLine_validEventCommand_success() throws Exception {
        Parser.ParsedCommand pc = Parser.parseCommandLine("event team sync /from 2019-12-02 0900 /to 2019-12-02 1000");
        assertEquals("event", pc.command());
        assertEquals("team sync /from 2019-12-02 0900 /to 2019-12-02 1000", pc.args());
    }
}
