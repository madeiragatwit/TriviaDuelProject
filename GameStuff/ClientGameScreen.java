
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGameScreen extends Application implements Initializable{

	public Label[] pointTexts = new Label[10];
	public static Label[] pointTextsS = new Label[10];
	public static boolean canPress = false;
	
	@FXML
	Label questionText;
	public static Label questionTextS;
	
	@FXML
	Label answer1text;
	public static Label answer1textS;
	@FXML
	Label answer2text;
	public static Label answer2textS;
	@FXML
	Label answer3text;
	public static Label answer3textS;
	@FXML
	Label answer4text;
	public static Label answer4textS;
	
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
	
	@FXML
	Button answerButton1;
	public static Button answerButton1S;
	
	@FXML
	Button answerButton2;
	public static Button answerButton2S;
	
	@FXML
	Button answerButton3;
	public static Button answerButton3S;
	
	@FXML
	Button answerButton4;
	public static Button answerButton4S;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pointTexts[0] = player1points;
		pointTexts[1] = player2points;
		pointTexts[2] = player3points;
		pointTexts[3] = player4points;
		pointTexts[4] = player5points;
		pointTexts[5] = player6points;
		pointTexts[6] = player7points;
		pointTexts[7] = player8points;
		pointTexts[8] = player9points;
		pointTexts[9] = player10points;
		
		ClientGameScreen.pointTextsS = pointTexts;
		ClientGameScreen.questionTextS = questionText;
		ClientGameScreen.answer1textS = answer1text;
		ClientGameScreen.answer2textS = answer2text;
		ClientGameScreen.answer3textS = answer3text;
		ClientGameScreen.answer4textS = answer4text;
		ClientGameScreen.answerButton1S = answerButton1;
		ClientGameScreen.answerButton2S = answerButton2;
		ClientGameScreen.answerButton3S = answerButton3;
		ClientGameScreen.answerButton4S = answerButton4;
		
		for(int i = 0; i < pointTexts.length; i++) {
			pointTexts[i].setText("");
		}
	}
	
	public static void displayQuestion(String question) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				
				questionTextS.setText(question);
				
				try {
					TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				canPress = true;
			}
		});
	}
	
	
	public static void displayAnswers(String answers) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				String[] answersArray = answers.split("]");
				for(int i = 0 ; i < answersArray.length; i++) {
					System.out.println(answersArray[i]);
				}
				answer1textS.setText(answersArray[0]);
				answer2textS.setText(answersArray[1]);
				answer3textS.setText(answersArray[2]);
				answer4textS.setText(answersArray[3]);
			}
		});
	}
	
	public static void displayMessage(String message) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				try {
					questionTextS.setText(message);
				}catch(Exception e) {
					
				}
			}
		});
	}
	
	
	public static void displayPoints(String points) {
		String[] pointStrings = points.split(" ");
		ArrayList<String> playerNames = ClientLobbyRoom.getPlayerNames();
		
		Platform.runLater(new Runnable() {
			@Override public void run() {
				for(int i = 0; i < pointStrings.length; i++) {
					try {
						pointTextsS[i].setText(playerNames.get(i) + ": " + pointStrings[i]);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	public void sendAnswer1(ActionEvent event) {
		if(canPress) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				sendAnswer1S();
				
				answer1textS.setText("");
				answer2textS.setText("");
				answer3textS.setText("");
				answer4textS.setText("");
				
				displayMessage("Waiting for others to answer...");
				
				canPress = false;
			}
		});
		}
	}
	
	public static void sendAnswer1S() {
		GameClient.sendAnswer(answer1textS.getText());
	}
	
	public void sendAnswer2(ActionEvent event) {
		if(canPress) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				sendAnswer2S();
				
				answer1textS.setText("");
				answer2textS.setText("");
				answer3textS.setText("");
				answer4textS.setText("");
				
				displayMessage("Waiting for others to answer...");
				
				canPress = false;
			}
		});
		}
	}
	
	public static void sendAnswer2S() {
		GameClient.sendAnswer(answer2textS.getText());
	}
	
	public void sendAnswer3(ActionEvent event) {
		if(canPress) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				sendAnswer3S();
				
				answer1textS.setText("");
				answer2textS.setText("");
				answer3textS.setText("");
				answer4textS.setText("");
				
				displayMessage("Waiting for others to answer...");
				
				canPress = false;
			}
		});
		}
	}
	
	public static void sendAnswer3S() {
		GameClient.sendAnswer(answer3textS.getText());
	}
	
	public void sendAnswer4(ActionEvent event) {
		if(canPress) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				sendAnswer4S();
				
				answer1textS.setText("");
				answer2textS.setText("");
				answer3textS.setText("");
				answer4textS.setText("");
				
				displayMessage("Waiting for others to answer...");
				
				canPress = false;
			}
		});
		}
	}
	
	public static void sendAnswer4S() {
		GameClient.sendAnswer(answer4textS.getText());
	}
	
	public static void setCorrect(boolean isCorrect) {
		System.out.println("isCorrect = " + isCorrect);
		if(isCorrect) {
			displayMessage("Correct!");
		}else {
			displayMessage("Incorrect...");
		}
	}
	
	public static void displayWinners(String winners) {
		String[] players = winners.split(" ");
		String toDisplay = "";
		if(players.length == 1) {
			displayMessage(players[0] + " won!");
		}else {
			for(int i = 0; i < players.length-1; i++) {
				toDisplay += players[i] + " and ";
			}
			toDisplay += players[players.length-1] + " won!";
			displayMessage(toDisplay);
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {
		
	}

}
