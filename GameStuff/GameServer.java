import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    /*
     * Method to create a new game with:
     * 	- new GameInstance (the game Thread)
     * 	- according Integer key (room code)
     */
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
 * (Each player connection is assigned to a new Thread)
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
            
            boolean allSet = false;
            while(!allSet) {
            String createOrJoin = reader.readLine();
            if(createOrJoin.substring(0,5).equals("*join")){
                String joinCode = createOrJoin.substring(5, 9);
                if(joinCode.length() == 4){
                    try{
                    	//If game exists
                        if(GameServer.games.containsKey(Integer.parseInt(joinCode))) {
                        	//If game has enough room
                        	if(GameServer.games.get(Integer.parseInt(joinCode)).addPlayer(this)) {
                        		allSet = true;
                        		writer.println("*allsetjoin");
                        	}else {
                        		writer.println("*gamefull");
                        	}
                        }else {
                        	writer.println("*noexist");
                        }
                        //If code contains non-numeric characters
                    }catch(NumberFormatException e){
                        writer.println("Invalid room code. Enter another code: ");
                    }
                }else {
                	writer.println("Invalid room code. Enter another code: ");
                }
            }else if(createOrJoin.equals("*create")){
            	//Creates room with random code & assigns player to room
                int code = GameServer.createGame(this, GameServer.games);
                System.out.println("Game created with code " + code + ".");
                writer.println(new String("*code" + code));
                allSet = true;
            }
            }
            
            //Code to run once player is in a lobby
            boolean started = false;
            while(true) {
            	String response = reader.readLine();
            	if(!started) {
            		//Will run until game is started ('started' is set to 'true')
            	if(response.equals("*startgame")) {
            		GameInstance game = getGame();
            		if(game.isFirstPlayer(this)) {
            			getGame().startGame();
            			started = true;
            		}else {
            			//Informs player that only party leader can start game
            			writer.println("*notleader");
            		}
            	}
            	}
            }

        }catch(Exception e){
            if(!name.equals("")) {
            	System.out.println(socket + " (" + name + ") disconnected.");
            }else {
            	System.out.println(socket + " disconnected.");
            }
        }
    }
    
    /*
     * Returns specific 'GameInstance' Thread that contains the player's 'User' Thread
     */
    public GameInstance getGame() {
    	Iterator it = GameServer.games.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<Integer, GameInstance> pair = (Map.Entry<Integer, GameInstance>)it.next();
    		if(pair.getValue().containsPlayer(this)) {
    			return pair.getValue();
    		}
    	}
    	//Code below should be unreachable, since player has to be in a game for this method to run
    	return null;
    }
}

/*
 * A multi-threaded class to run separate instances of games
 * (Each GameInstance is stored in a HashMap in the server with a key (the room code)
 */
class GameInstance extends Thread{
    private int gameCode;
    private HashMap<User, Integer> players;
    private boolean isStarted = false;
    
    public User firstPlayer;

    public GameInstance(User player, int gameCode){
        this.players = new HashMap<User, Integer>();
        players.put(player, 0);
        firstPlayer = player;
        broadcastMessage("*players" + sendCurrentPlayers());
        this.gameCode = gameCode;
    }

    /*
     * Adds new player to existing HashMap of Users
     */
    public boolean addPlayer(User player) {
    	if(players.size() == 10) {
    		//Returns false if room is already full
    		return false;
    	}else {
    		//Room is not full; proceed with adding new player
    		players.put(player, 0);
    		broadcastMessage("*players" + sendCurrentPlayers());
    		sendJoinMessage(player);
    		return true;
    	}
    }
    
    /*
     * Sends all current players a message notifying them of a new User joining
     */
    void sendJoinMessage(User player) {
    	Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(pair.getKey() != player) {
    			pair.getKey().writer.println("Player " + player.name + " has joined!");
    		}
    		
    	}
    }
    
    /*
     * Sends list of all players including the player itself.
     */
    String sendCurrentPlayers() {
    	String output = "";
    	Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		output += pair.getKey().name + " ";
    		
    	}
    	
    	return output;
    }
    
    /*
     * Sends all players a given String message
     */
    public void broadcastMessage(String message) {
    	Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		pair.getKey().writer.println(message);
    		
    	}
    }
    
    /*
     * Checks if given 'User' Thread is present in player HashMap
     */
    public boolean containsPlayer(User player) {
    	Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(pair.getKey() == player) {
    			return true;
    		}
    		
    	}
    	return false;
    }
    
    /*
     * Checks if given 'User' Thread is in the first position of player ArrayList
     * (meaning that the player is the party leader)
     */
    public boolean isFirstPlayer(User player) {
    	if(firstPlayer == player) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    /*
     * Returns amount of players in lobby
     */
    public int getCurrPlayers() {
    	return players.size();
    }
    
    /*
     * Informs every player to start the game
     */
    public void startGame() {
    	broadcastMessage("*startgame");
    	isStarted = true;
    }
    
    /*
     * Code for actual game once started
     */
    public void run() {
    	while(true) {
    		if(isStarted) {
    			try {
    				TimeUnit.MILLISECONDS.sleep(5);
    			} catch (InterruptedException e1) {
    				e1.printStackTrace();
    			}
    			
    			
    		}
    	}
    }
}
