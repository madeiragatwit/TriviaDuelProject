import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameServer {

    //HashMap of games to keep track of which game instances have certain room codes
    public static HashMap<GameInstance, Integer> games = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);

        //ArrayList to keep track of overall players
        ArrayList<User> players = new ArrayList<>();

        System.out.println("Server is ready to receive.");

        while(true){
            Socket connection = serverSocket.accept();

            User player = new User(connection);
            players.add(player);
            player.start();
        }
    }

    public static int createGame(User player, HashMap games){
        //Generate a random room code to assign to new game
        Random r = new Random();
        int code = r.nextInt(8999) + 1000;

        //Keep generating a random room code until there is a unique one
        while(games.containsValue(code)){
            code = r.nextInt(8999) + 1000;
        }

        //Create the new game & put it in the HashMap
        GameInstance game = new GameInstance(player, code);
        games.put(game, code);
        return code;
    }
}

//User class to interact with each player connection
class User extends Thread{
    private Socket socket;
    private GameServer server;
    private PrintWriter writer;
    private String name;

    public User(Socket s){
        this.socket = s;
    }

    public void run(){
        try{
            InputStream in = socket.getInputStream();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(out, true);
            System.out.println("hello");

            String nameInput = reader.readLine();
            System.out.println("hello2");
            System.out.println(name + " has connected.");
            this.name = nameInput;
            System.out.println("hello3");

            String createOrJoin = reader.readLine();
            if(createOrJoin.equals("join")){
                String input = reader.readLine();
                if(input.length() == 4){
                    try{
                        int code = Integer.parseInt(input);

                    }catch(NumberFormatException e){
                        out.writeBytes(new String("Invalid room code."));
                    }
                }
            }else if(createOrJoin.equals("create")){
                int code = GameServer.createGame(this, GameServer.games);
                System.out.println("Game created with code " + code);
                out.writeBytes(new String("Game created with code " + code));
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

//Class to run separate instances of games
class GameInstance extends Thread{
    private int gameCode;
    private ArrayList<User> players;

    public GameInstance(User player, int gameCode){
        this.players = new ArrayList<>();
        players.add(player);
        this.gameCode = gameCode;
    }

    public void addPlayer(User player){

    }
}
