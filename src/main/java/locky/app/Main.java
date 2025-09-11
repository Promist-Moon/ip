package locky.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main class where the app runs.
 */
public class Main extends Application {
    private Locky locky = new Locky("./data/locky.txt");

    /**
     * @param stage the primary stage for this application, onto which
     *         the application scene can be set.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainWindow controller = fxmlLoader.getController();
        controller.setLocky(locky);

        stage.setTitle("Locky — Lock In!");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Locky.png")));
        stage.setScene(scene);
        stage.show();
    }
}
