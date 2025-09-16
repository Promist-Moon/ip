package locky.app;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        boolean isEnabled = false;
        assert isEnabled = true;
        Application.launch(Main.class, args);
    }
}

