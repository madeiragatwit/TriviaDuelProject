import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGameScreen extends Application implements Initializable{

	@FXML
	Label questionText;
	
	@FXML
	Label answer1text;
	@FXML
	Label answer2text;
	@FXML
	Label answer3text;
	@FXML
	Label answer4text;
	
	@FXML
	Label player1points;
	@FXML
	Label player2points;
	@FXML
	Label player3points;
	@FXML
	Label player4points;
	@FXML
	Label player5points;
	@FXML
	Label player6points;
	@FXML
	Label player7points;
	@FXML
	Label player8points;
	@FXML
	Label player9points;
	@FXML
	Label player10points;
	
	@FXML
	Button answer1button;
	@FXML
	Button answer2button;
	@FXML
	Button answer3button;
	@FXML
	Button answer4button;
	
	@FXML
	Label playerWinText;
	
	@FXML
	Button leaveButton;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public static void displayQuestion(String question) {
		
	}
	
	public static void displayAnswers(String[] answers) {
		
	}

	@Override
	public void start(Stage arg0) throws Exception {
		
	}

}
