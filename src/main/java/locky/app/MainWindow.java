package locky.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main Locky GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Locky locky;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image lockyImage = new Image(this.getClass().getResourceAsStream("/images/Locky.png"));

    /**
     * Initializes the controller after the FXML elements are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Locky instance.
     */
    public void setLocky(Locky l) {
        locky = l;
        dialogContainer.getChildren().add(
                DialogBox.getLockyDialog(locky.getGreeting(), lockyImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Locky's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = locky.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getLockyDialog(response, lockyImage)
        );
        userInput.clear();
    }
}

