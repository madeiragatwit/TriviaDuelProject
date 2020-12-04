import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 
 *
 * @author madeirag
 * @version 1.0.0 2020-12-03 Initial implementation
 *
 */
public class MainMenu extends Application implements Initializable {
	@FXML
	Button myButton;
	@FXML
	Text myText;

	@Override
	public void start(Stage primaryStage) throws Exception {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("TriviaGame.fxml"));
		final Pane p = loader.load();
		primaryStage.setScene(new Scene(p));
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		myButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				myText.setText(String.format("Value: %d", (new Random()).nextInt(100)));
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}
// end class MainMenu