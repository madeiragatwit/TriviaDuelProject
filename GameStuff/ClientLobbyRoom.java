
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
	
	Text[] playerLabels = new Text[10];
	static Text[] playerLabelsS = new Text[10];
	static ArrayList<String> playerNames = new ArrayList<String>();
	
	@FXML
	Text player1text;
	@FXML
	Text player2text;
	@FXML
	Text player3text;
	@FXML
	Text player4text;
	@FXML
	Text player5text;
	@FXML
	Text player6text;
	@FXML
	Text player7text;
	@FXML
	Text player8text;
	@FXML
	Text player9text;
	@FXML
	Text player10text;
	
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
	public static int playerCount = 0;

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
		
		ClientLobbyRoom.playerLabelsS = playerLabels;
		
		for(int i = 0; i < playerLabels.length; i++) {
			playerLabels[i].setText("");
		}
		
		ClientLobbyRoom.leaderTextS = leaderText;
		
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		setPlayerNames();
		playerCount = 1;
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		roomCode.setText(roomCodeS);
	}
	
	/*
	 * Gets player names from Client and adds them to ArrayList of names
	 */
	public static void setPlayerNamesS(String s) {
		String[] tempNames = s.split(" ");
		while(!playerNames.isEmpty()) {
			playerNames.remove(0);
		}
		Collections.addAll(reversePlayers(playerNames), tempNames);
		System.out.println(playerNames);
		setPlayerNames();
		playerCount = playerNames.size();
	}
	
	public static ArrayList<String> reversePlayers(ArrayList<String> list) {
		for(int i = 0; i < list.size() / 2; i++) {
			String temp = list.get(i);
			list.set(i, list.get(list.size()-i-1));
			list.set(list.size()-i-1, temp);
		}
		return list;
	}
	
	/*
	 * Sets player name texts to according names from ArrayList
	 */
	public static void setPlayerNames() {
		for(int i = 0; i < playerNames.size(); i++) {
			try {
				playerLabelsS[i].setText(playerNames.get(i));
			}catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<String> getPlayerNames() {
		return playerNames;
	}
	
	/*
	 * Updates player name texts with new players
	 */
	public static void updatePlayerNames() {
		for(int i = 0; i < playerNames.size(); i++) {
			playerLabelsS[i].setText(playerNames.get(i));
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
	
	public static void setNotEnough() {
		leaderTextS.setText("Not enough players to start!");
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
