
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
    
    public boolean hasWon = false;
    public boolean hasAnswered = false;
    public String answer = "";

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
            while(true) {
            	String response = reader.readLine();
            	if(response.equals("*startgame")) {
            		GameInstance game = getGame();
            		if(game.isFirstPlayer(this) && game.getCurrPlayers() > 1) {
            			getGame().startGame();
            			continue;
            		}else if(game.getCurrPlayers() < 2){
            			//Informs the player that there arent enough people to start
            			writer.println("*notenoughplayers");
            		}else {
            			//Informs player that only party leader can start game
            			writer.println("*notleader");
            		}
            	}
            	if(response.substring(0,7).equals("*answer")) {
            			this.answer = response.substring(7, response.length());
            			System.out.println(this.name + ": " + this.answer);
            			this.hasAnswered = true;
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
    private LinkedHashMap<User, Integer> players;
    private boolean isStarted = false;
    
    public User firstPlayer;

    public GameInstance(User player, int gameCode){
        this.players = new LinkedHashMap<User, Integer>();
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
    	ArrayList<String> names = new ArrayList<String>();
    	String output = "";
    	Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		names.add(pair.getKey().name);
    		//output += pair.getKey().name + " ";
    	}
    	
    	for(int i = 0; i < names.size() / 2; i++) {
			String temp = names.get(i);
			names.set(i, names.get(names.size()-i-1));
			names.set(names.size()-i-1, temp);
		}
    	
    	for(int i = names.size()-1; i >= 0; i--) {
    		output += names.get(i) + " ";
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
    	this.start();
    }
    
    
    private static String streamToString(InputStream inputStream) {
	    String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
	    return text;
	  }
	
    /*
     * Returns String representation of JSON file retrieved from URL
     */
	public static String jsonGetRequest(String urlQueryString) {
	    String json = null;
	    try {
	      URL url = new URL(urlQueryString);
	      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	      connection.setDoOutput(true);
	      connection.setInstanceFollowRedirects(false);
	      connection.setRequestMethod("GET");
	      connection.setRequestProperty("Content-Type", "application/json");
	      connection.setRequestProperty("charset", "utf-8");
	      connection.connect();
	      InputStream inStream = connection.getInputStream();
	      json = streamToString(inStream); // input stream to string
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    return json;
	  }
	
	public void displayPoints() {
		int[] points = new int[players.size()];
		int i = 0;
		Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		points[i] = pair.getValue();
    		i++;
    	}
    	
    	String output = "";
    	for(int j = 0; j < points.length; j++) {
    		output += points[j] +  " ";
    	}
    	broadcastMessage("*points" + output);
	}
	
	public boolean didAnybodyWin() {
		Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(pair.getValue() == 10) {
    			return true;
    		}
    	}
    	return false;
	}
	
	public String[] getTrivia(String json) {
		String[] result = new String[6];
		
		int questionPositionStart = json.indexOf("QUESTION") + 11;
		int questionPositionEnd = json.indexOf("ansA") - 3;
		result[0] = json.substring(questionPositionStart, questionPositionEnd);
		
		int answer1PositionStart = json.indexOf("ansA") + 7;
		int answer1PositionEnd = json.indexOf("ansB") - 3;
		result[1] = json.substring(answer1PositionStart, answer1PositionEnd);
		
		int answer2PositionStart = json.indexOf("ansB") + 7;
		int answer2PositionEnd = json.indexOf("ansC") - 3;
		result[2] = json.substring(answer2PositionStart, answer2PositionEnd);
		
		int answer3PositionStart = json.indexOf("ansC") + 7;
		int answer3PositionEnd = json.indexOf("ansD") - 3;
		result[3] = json.substring(answer3PositionStart, answer3PositionEnd);
		
		int answer4PositionStart = json.indexOf("ansD") + 7;
		int answer4PositionEnd = json.indexOf("created_at") - 3;
		result[4] = json.substring(answer4PositionStart, answer4PositionEnd);
		
		int correctAnswerPositionStart = json.indexOf("correctAns") + 13;
		int correctAnswerPositionEnd = json.indexOf("message") - 4;
		result[5] = json.substring(correctAnswerPositionStart, correctAnswerPositionEnd);
		
		return result;
	}
	
	public boolean hasEverybodyAnswered() {
		Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		System.out.print("");
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(!pair.getKey().hasAnswered) {
    			return false;
    		}
    	}
    	return true;
	}
	
	public void calculatePoints(String correctAnswer) {
		Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(pair.getKey().answer.equals(correctAnswer)) {
    			//Increment points for correct answer
    			int newValue = pair.getValue() + 1;
    			pair.setValue(newValue);
    			pair.getKey().writer.println("*gotcorrect");
    		}else {
    			pair.getKey().writer.println("*gotincorrect");
    		}
			//Reset answer & hasAnswered
			pair.getKey().answer = "";
			pair.getKey().hasAnswered = false;
    	}
	}
	
	public String getWinners() {
		String output = "";
		Iterator it = players.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<User, Integer> pair = (Map.Entry<User, Integer>)it.next();
    		if(pair.getValue() == 10) {
    			output += pair.getKey().name + " ";
    		}
    	}
    	return output;
	}

    
    /*
     * Code for actual game once started
     */
    public void run() {	
    	while(true) {
    		if(isStarted) {
    			broadcastMessage("*messageReady to play?");
    			displayPoints();
    			try {
    				TimeUnit.SECONDS.sleep(2);
    			} catch (InterruptedException e1) {
    				e1.printStackTrace();
    			}
    			
    			//Runs while game is still going; nobody has reached 5 points
    			while(!didAnybodyWin()) {
    				String newLine = jsonGetRequest("http://zenith.blue:8082/questions/rnd?k=98327493298");
    				String[] trivia = getTrivia(newLine);
    				
    				String question = trivia[0];
    				String answer1 = trivia[1];
    				String answer2 = trivia[2];
    				String answer3 = trivia[3];
    				String answer4 = trivia[4];
    				String correctAnswer = trivia[5];
    				
    				
    				broadcastMessage("*question" + question);
    				broadcastMessage("*answers" + answer1 + "]" + answer2 + "]" + answer3 + "]" + answer4);
    				
    				while(!hasEverybodyAnswered()) {
    					//Waiting for players to answer
    				}
    				
    				calculatePoints(correctAnswer);
    				displayPoints();
    				if(didAnybodyWin()) {
    					broadcastMessage("*winners" + getWinners());
    					isStarted = false;
    				}
    				
    				try {
    					TimeUnit.SECONDS.sleep(3);
    				} catch (InterruptedException e1) {
    					e1.printStackTrace();
    				}
    			}
    			
    			/*
    			 * Game code
    			 */
    		}
    	}
    }
}
