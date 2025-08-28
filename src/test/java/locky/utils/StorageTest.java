package locky.utils;

import locky.tasks.Task;
import locky.tasks.Todo;
import locky.tasks.Deadline;
import locky.tasks.Event;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    /**
     * Helper to invoke the private parseLine(String) method
     * @param s
     * @param line
     * @return
     * @throws Exception
     */
    private Task parseLine(Storage s, String line) throws Exception {
        Method m = Storage.class.getDeclaredMethod("parseLine", String.class);
        m.setAccessible(true);
        try {
            return (Task) m.invoke(s, line);
        } catch (InvocationTargetException ite) {
            Throwable c = ite.getCause();
            if (c instanceof RuntimeException re) throw re;
            throw ite;
        }
    }

    @Test
    public void parseLine_validTodo_parsed() throws Exception {
        Storage s = new Storage("unused.txt");
        Task t = parseLine(s, "T|0|buy milk");
        assertNotNull(t);
        assertTrue(t instanceof Todo);
        Todo todo = (Todo) t;
        assertEquals("buy milk", todo.getDescription());
        assertFalse(todo.getDone());
    }

    @Test
    public void parseLine_validDeadline_parsed() throws Exception {
        Storage s = new Storage("unused.txt");
        Task t = parseLine(s, "D|1|submit report|2019-12-02 1800");
        assertNotNull(t);
        assertInstanceOf(Deadline.class, t);
        Deadline d = (Deadline) t;
        assertEquals("submit report", d.getDescription());
        assertTrue(d.getDone());
        assertEquals(LocalDateTime.of(2019,12,2,18,0), d.getDeadline());
    }

    @Test
    void parseLine_validEvent_parsed() throws Exception {
        Storage s = new Storage("unused.txt");
        Task t = parseLine(s, "E|0|team sync|2019-12-02 0900|2019-12-02 1000");
        assertNotNull(t);
        assertInstanceOf(Event.class, t);
        Event e = (Event) t;
        assertEquals("team sync", e.getDescription());
        assertFalse(e.getDone());
        assertEquals(LocalDateTime.of(2019,12,2,9,0),  e.getStart());
        assertEquals(LocalDateTime.of(2019,12,2,10,0), e.getEnd());
    }

    @Test
    void parseLine_tooFewFields_returnsNull() throws Exception {
        Storage s = new Storage("unused.txt");
        assertNull(parseLine(s, "D|0|missing date"));
        assertNull(parseLine(s, "E|1|only start"));
        assertNull(parseLine(s, "T|1")); // missing description
    }

    @Test
    void parseLine_unknownType_returnsNull() throws Exception {
        Storage s = new Storage("unused.txt");
        assertNull(parseLine(s, "X|1|whatever"));
    }

    @Test
    void parseLine_badDateInDeadline_throwsIllegalArgumentException() {
        Storage s = new Storage("unused.txt");
        assertThrows(IllegalArgumentException.class,
                () -> parseLine(s, "D|0|bad date ex|12/02/2019 6pm"));
    }

    @Test
    void parseLine_badDateInEventStartOrEnd_throwsIllegalArgumentException() {
        Storage s = new Storage("unused.txt");
        assertThrows(IllegalArgumentException.class,
                () -> parseLine(s, "E|0|mtg|2019/12/02 09:00|2019-12-02 1000"));
        assertThrows(IllegalArgumentException.class,
                () -> parseLine(s, "E|0|mtg|2019-12-02 0900|02-12-2019 10:00"));
    }
}
