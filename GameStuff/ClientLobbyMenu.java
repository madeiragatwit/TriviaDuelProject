
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientLobbyMenu extends Application implements Initializable{
	
	@FXML
	Button createButton;
	@FXML
	Button joinButton;
	@FXML
	Text nameText;
	@FXML
	TextField roomCode;
	@FXML
	Text errorText;
	public static Text errorTextS;
	
	public static boolean setToJoinS = false;
	
	@Override
	public void start(Stage arg0) throws Exception {
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		nameText.setText(GameClient.name);
		ClientLobbyMenu.errorTextS = errorText;
	}
	
	/*
	 * Checks if entered code is valid, and sends it to server to check if
	 * game is joinable, and puts the player into lobby if so
	 */
	public void joinGame(ActionEvent event) {
		String code = roomCode.getText();
		if(code.length() == 4 && code.matches("[0-9]+")) {
			GameClient.sendJoinCode(code);
			
			try {
				TimeUnit.MILLISECONDS.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			if(setToJoinS) {
				GameClient.setRoomCode(code);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyRoom.fxml"));
				try {
					Pane p = loader.load();
					joinButton.getScene().setRoot(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			errorText.setText("Invalid room code, must be 4 numbers.");
		}
	}
	
	/*
	 * Informs the server that user wants to create a new game,
	 * and switches scene to lobby
	 */
	public void createGame() {
		GameClient.sendCreate();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyRoom.fxml"));
		try {
			Pane p = loader.load();
			createButton.getScene().setRoot(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Methods below handle codes from client and act accordingly
	 */
	public static void gameDoesntExist() {
		errorTextS.setText("Game with that code doesn't exist.");
	}
	
	public static void gameFull() {
		errorTextS.setText("Game is full.");
	}
	
	public static void setSetToJoin() {
		setToJoinS = true;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
