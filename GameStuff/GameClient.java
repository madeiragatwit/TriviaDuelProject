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
import javafx.stage.Stage;

public class GameClient extends Application implements Initializable {
	String host;
	int port;
	static String name;

	@FXML
	Button serverButton;
	@FXML
	TextField nameField;

	@Override
	public void start(Stage primaryStage) throws Exception {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
		final Pane p = loader.load();
		primaryStage.setScene(new Scene(p));
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		serverButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				name = nameField.getText();
				host = "localhost";
				port = 1234;
				run();
			}
		});
	}

	public static void main(String[] args) throws IOException {
		launch(args);

	}

	public void run() {
		try {
			Socket s = new Socket(host, port);
			new Read(s, this).start();
			new Write(s, this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				String response = reader.readLine();
				System.out.println(response);
			} catch (Exception e) {
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
	private PrintWriter write;

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
		Scanner n = new Scanner(System.in);
		boolean isCreating = false;

		// String to determine if player is creating or joining a room (will change to
		// methods for UI buttons in the future)
		System.out.println("Create or join room?: ");
		String answer = n.nextLine();
		if (answer.equals("create")) {
			isCreating = true;
		}
		write.println(answer);

		if (!isCreating) {
			System.out.print("Enter a room code: ");
			String code = n.nextLine();
			write.println(code);
		}

		String input = "";
		while (true) {
			input = n.nextLine();
			write.println(input);
		}
	}
}