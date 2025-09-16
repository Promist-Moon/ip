package locky.app;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a dialog box with specified message text and avatar image.
     * @param text message to display in dialog box
     * @param img speaker's avatar image
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
            // allow bubble to grow in width
            dialog.maxWidthProperty().bind(
                    javafx.beans.binding.Bindings.min(
                            this.widthProperty().multiply(0.7), // use 70% of available row width
                            600
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a DialogBox for user input through factory method.
     * @param text message to display in dialog box
     * @param img speaker's avatar image
     * @return DialogBox styled for Locky's responses
     */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        HBox.setHgrow(db, javafx.scene.layout.Priority.ALWAYS);
        db.dialog.maxWidthProperty().bind(db.widthProperty().multiply(0.7));
        db.setAlignment(Pos.TOP_RIGHT);
        return db;
    }

    /**
     * Creates a DialogBox for Locky's responses through factory method.
     * @param text message to display in dialog box
     * @param img speaker's avatar image
     * @return DialogBox styled for Locky's responses
     */
    public static DialogBox getLockyDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
