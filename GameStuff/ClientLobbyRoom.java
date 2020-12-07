import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientLobbyRoom extends Application implements Initializable{
	
	static Label[] playerLabels = new Label[10];
	static Label[] playerLabelsS = new Label[10];
	static ArrayList<String> playerNames = new ArrayList<String>();
	
	@FXML
	Label player1text;
	@FXML
	Label player2text;
	@FXML
	Label player3text;
	@FXML
	Label player4text;
	@FXML
	Label player5text;
	@FXML
	Label player6text;
	@FXML
	Label player7text;
	@FXML
	Label player8text;
	@FXML
	Label player9text;
	@FXML
	Label player10text;
	
	@FXML
	Text leaderText;
	public static Text leaderTextS;
	
	@FXML
	Text roomCode;
	public static String roomCodeS;
	
	@FXML
	Button startButton;
	@FXML
	Button leaveButton;
	
	public static boolean canStart = false;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		playerLabels[0] = player1text;
		playerLabels[1] = player2text;
		playerLabels[2] = player3text;
		playerLabels[3] = player4text;
		playerLabels[4] = player5text;
		playerLabels[5] = player6text;
		playerLabels[6] = player7text;
		playerLabels[7] = player8text;
		playerLabels[8] = player9text;
		playerLabels[9] = player10text;
		
		for(int i = 0; i < playerLabels.length; i++) {
			playerLabels[i].setText("");
		}
		
		ClientLobbyRoom.leaderTextS = leaderText;
		
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		roomCode.setText(roomCodeS);
		setPlayerNames();
	}
	
	/*
	 * Gets player names from Client and adds them to ArrayList of names
	 */
	public static void setPlayerNamesS(String s) {
		String[] tempNames = s.split(" ");
		Collections.addAll(playerNames, tempNames);
	}
	
	/*
	 * Sets player name texts to according names from ArrayList
	 */
	public static void setPlayerNames() {
		for(int i = 0; i < playerNames.size(); i++) {
			playerLabels[i].setText(playerNames.get(i));
		}
	}
	
	/*
	 * Updates player name texts with new players
	 */
	public static void updatePlayerNames() {
		for(int i = 0; i < playerNames.size(); i++) {
			playerLabels[i].setText(playerNames.get(i));
		}
	}
	
	/*
	 * Sets room code text to room code
	 */
	public static void setRoomCode(String s) {
		roomCodeS = s;
	}
	
	/*
	 * Informs player if they are not the party leader if they try to start game
	 */
	public static void setNotLeader() {
		leaderTextS.setText("Only party leader can start!");
	}
	
	/*
	 * Action method for start button
	 */
	public void buttonStart() {
		startGame();
	}
	
	/*
	 * Calls client to send start code to server
	 */
	public static void startGame() {
		GameClient.startGame();
	}
	
	@Override
	public void start(Stage arg0) throws Exception {
	}

}
