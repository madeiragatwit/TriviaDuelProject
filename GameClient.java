import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
	
	String host;
	int port;
	
	public GameClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
    public static void main(String[] args) throws IOException {
        GameClient client = new GameClient("localhost", 1234);
        client.run();

    }
    
    public void run() {
    	try {
    		Socket s = new Socket(host, port);
    		System.out.println("Connected.");
    		
    		new Read(s, this).start();
    		new Write(s, this).start();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}

class Read extends Thread{
    private Socket socket;
    private GameClient client;
    private BufferedReader reader;

    public Read(Socket s, GameClient c){
        this.socket = s;
        this.client = c;
        try{
            InputStream in = s.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            try{
                String response = reader.readLine();
                System.out.println(response);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

class Write extends Thread{
    private Socket socket;
    private GameClient client;
    private PrintWriter write;

    public Write(Socket s, GameClient c){
        this.socket = s;
        this.client = c;
        try{
            OutputStream out = s.getOutputStream();
            write = new PrintWriter(out, true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        Scanner n = new Scanner(System.in);
        boolean isCreating = false;

        //Player name
        System.out.print("Enter your name: ");
        String name = n.nextLine();
        write.println(name);

        //String to determine if player is creating or joining a room (will change to methods for UI buttons in the future)
        System.out.println("Create or join room?: ");
        String answer = n.nextLine();
        if(answer.equals("create")){
            isCreating = true;
        }
        write.println(answer);
        
        if(!isCreating) {
            System.out.print("Enter a room code: ");
            String code = n.nextLine();
            write.println(code);
        }

        String input = "";
        while(true){
            input = n.nextLine();
            write.println(input);
        }
    }
}