import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameServer {

    //HashMap of games to keep track of which game instances have certain room codes
    public static HashMap<Integer, GameInstance> games = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);

        //ArrayList to keep track of overall players
        ArrayList<User> players = new ArrayList<>();

        System.out.println("Server is ready to receive.");

        while(true){
            Socket connection = serverSocket.accept();
            System.out.println(connection + " connected.");

            User player = new User(connection);
            players.add(player);
            player.start();
        }
    }

    public static int createGame(User player, HashMap<Integer, GameInstance> games){
        //Generate a random room code to assign to new game
        Random r = new Random();
        int code = r.nextInt(8999) + 1000;

        //Keep generating a random room code until there is a unique one
        while(games.containsValue(code)){
            code = r.nextInt(8999) + 1000;
        }

        //Create the new game & put it in the HashMap
        GameInstance game = new GameInstance(player, code);
        games.put(code, game);
        return code;
    }
}

/*
 * User class to interact with each individual player connection.
 */
class User extends Thread{
    private Socket socket;
    public PrintWriter writer;
    public String name = "";

    public User(Socket s) {
        this.socket = s;
    }

    public void run() {
        try{
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(out, true);

            name = reader.readLine();

            String createOrJoin = reader.readLine();
            if(createOrJoin.equals("join")){
            	boolean allSetJoin = false;
            	while(!allSetJoin) {
                String joinCode = reader.readLine();
                if(joinCode.length() == 4){
                    try{
                    	//If game exists
                        if(GameServer.games.containsKey(Integer.parseInt(joinCode))) {
                        	//If game has enough room
                        	if(GameServer.games.get(Integer.parseInt(joinCode)).addPlayer(this)) {
                        		allSetJoin = true;
                        	}else {
                        		writer.println("Game is full. Enter another code: ");
                        	}
                        }else {
                        	writer.println("Room does not exist with code " + joinCode + ". Enter another code: ");
                        }
                        //If code contains non-numeric characters
                    }catch(NumberFormatException e){
                        writer.println("Invalid room code. Enter another code: ");
                    }
                }else {
                	writer.println("Invalid room code. Enter another code: ");
                }
            	}
            }else if(createOrJoin.equals("create")){
            	//Creates room with random code & assigns player to room
                int code = GameServer.createGame(this, GameServer.games);
                System.out.println("Game created with code " + code + ".");
                writer.println(new String("Game created with code " + code + "!"));
            }

        }catch(Exception e){
            if(!name.equals("")) {
            	System.out.println(socket + " (" + name + ") disconnected.");
            }else {
            	System.out.println(socket + " disconnected.");
            }
        }
    }
}

/*
 * A multi-threaded class to run separate instances of games
 * (Each GameInstance is stored in a HashMap in the server with a key (the room code)
 */
class GameInstance extends Thread{
    private int gameCode;
    private ArrayList<User> players;

    public GameInstance(User player, int gameCode){
        this.players = new ArrayList<>();
        players.add(player);
        this.gameCode = gameCode;
    }

    /*
     * Adds new player to existing ArrayList of Users
     */
    public boolean addPlayer(User player) {
    	if(players.size() == 10) {
    		//Returns false if room is already full
    		return false;
    	}else {
    		//Room is not full; proceed with adding new player
    		players.add(player);
    		player.writer.println("Game " + gameCode + " joined! Other player(s): " + sendCurrentPlayers(player));
    		sendJoinMessage(player);
    		return true;
    	}
    }
    
    /*
     * Sends all current players a message notifying them of a new User joining
     */
    void sendJoinMessage(User player) {
    	for(int i = 0; i < players.size(); i++) {
    		if(players.get(i) != player) {
    			players.get(i).writer.println("Player " + player.name + " has joined!");
    		}
    	}
    }
    
    /*
     * Sends the new User a list of all current players separated by commas
     */
    String sendCurrentPlayers(User player) {
    	String output = "";
    	for(int i = 0; i < players.size()-1; i++) {
    		if(players.get(i) != player) {
    			output += players.get(i).name + ", ";
    		}
    	}
    	
    	//Sends back String without comma at the end
    	return output.substring(0, output.length()-2);
    }
    
    /*
     * Sends all players a given String message
     */
    public void broadcastMessage(String message) {
    	for(int i = 0; i < players.size(); i++) {
    		players.get(i).writer.println(message);
    	}
    }
    
    public int getCurrPlayers() {
    	return players.size();
    }
    
    public void run() {
    	broadcastMessage("Welcome to the game!");
    	/*
    	 * TO-DO:
    	 * Lobby & Game code
    	 * 
    	 */
    }
}