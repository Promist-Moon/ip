package locky.app;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        boolean enabled = false;
        assert enabled = true;
        Application.launch(Main.class, args);
    }
}

