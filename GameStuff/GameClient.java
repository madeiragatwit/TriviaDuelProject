import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameClient extends Application implements Initializable {
	String host;
	int port;
	static String name;
	
	public Stage window;
	
	public Scene welcomeScene, createOrJoinScene, lobbyScene, gameScene;

	@FXML
	Button serverButton;
	@FXML
	TextField nameField;
	@FXML
	Text nameErrorText;

	/*
	 * JavaFX start method
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
		Pane p = loader.load();
		welcomeScene = new Scene(p);
		
		
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Welcome");
		primaryStage.show();
	}

	/*
	 * JavaFX initialize method
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		serverButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				name = nameField.getText();
				if(!name.equals("")) {
					host = "localhost";
					port = 1234;
					
					FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyMenu.fxml"));
					try {
						Pane p = loader.load();
						serverButton.getScene().setRoot(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					run();
				}else {
					nameErrorText.setText("Please enter a name.");
				}
			}
		});
	}


	/*
	 * Client run method
	 */
	public void run() {
		try {
			Socket s = new Socket(host, port);
			new Read(s, this).start();
			new Write(s, this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns the user's player name
	 */
	public static String getName() {
		return name;
	}
	
	/*
	 * Sends to the server the code of the room being attempted to join
	 */
	public static void sendJoinCode(String s) {
		Write.sendJoinCode(s);
	}
	
	/*
	 * Tells the server that the player wants to create a room
	 */
	public static void sendCreate() {
		Write.sendCreate();
	}
	
	/*
	 * Sets the room code text in the player lobby
	 */
	public static void setRoomCode(String s) {
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		ClientLobbyRoom.setRoomCode(s);
	}
	
	/*
	 * Tells the server to start the game
	 */
	public static void startGame() {
		Write.sendStart();
	}
	
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}

//Class to handle reading messages from server.
class Read extends Thread {
	private Socket socket;
	private GameClient client;
	private BufferedReader reader;

	public Read(Socket s, GameClient c) {
		this.socket = s;
		this.client = c;
		try {
			InputStream in = s.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				/*
				 * The server will send back codes starting with '*'. These are if statements to deciper the codes being sent back
				 * from the server, and the program acts accordingly.
				 */
				String response = reader.readLine();
				if(response.equals("*noexist")) {
					//If a game doesn't exist
					ClientLobbyMenu.gameDoesntExist();
				}else if(response.equals("*gamefull")) {
					//If a game is full
					ClientLobbyMenu.gameFull();
				}else if(response.equals("*allsetjoin")) {
					//If a player is good to join a lobby
					ClientLobbyMenu.setSetToJoin();
				}else if(response.substring(0, 8).equals("*players")) {
					//Sets each player name text in the player's lobby menu
					ClientLobbyRoom.setPlayerNamesS(response.substring(8, response.length()));
				}else if(response.substring(0, 5).equals("*code")) {
					try {
						TimeUnit.MILLISECONDS.sleep(5);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					//Sets room code text for player's lobby menu
					ClientLobbyRoom.setRoomCode(response.substring(5, 9));
				}else if(response.equals("*startgame")) {
					//Switches scenes to actual game once started
					FXMLLoader loader = new FXMLLoader(getClass().getResource("GameScreen.fxml"));
					try {
						Pane p = loader.load();
						ClientLobbyRoom.leaderTextS.getScene().setRoot(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(response.equals("*notleader")) {
					//Informs the player that only the party leader can start the game
					ClientLobbyRoom.setNotLeader();
				}
				//Debug
				System.out.println(response);
			} catch (Exception e) {
				//Occurs when connection with server terminates
				System.out.println("Disconnected from server.");
				System.exit(0);
			}
		}
	}
}

//Class to handle sending messages to server.
class Write extends Thread {
	private Socket socket;
	private GameClient client;
	private static PrintWriter write;
	
	@FXML
	Button joinButton;
	
	@FXML
	Button createButton;
	
	@FXML
	Text nameText;

	public Write(Socket s, GameClient c) {
		this.socket = s;
		this.client = c;
		try {
			OutputStream out = s.getOutputStream();
			write = new PrintWriter(out, true);
		} catch (Exception e) {
			System.exit(0);
		}
	}

	public void run() {
		write.println(GameClient.getName());
		
		while (true) {
			
		}
	}
	
	/*
	 * The methods below send codes starting with '*' to the server.
	 * The server deciphers the codes being sent and acts accordingly.
	 */
	
	public static void sendJoinCode(String s) {
		write.println("*join" + s);
	}
	
	public static void sendCreate() {
		write.println("*create");
	}
	
	public static void sendStart() {
		write.println("*startgame");
	}
}