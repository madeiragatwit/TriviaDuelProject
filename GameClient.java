import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) throws IOException {
        Socket connectionSocket = new Socket("localhost", 1234);

        new Read(connectionSocket).start();
        new Write(connectionSocket).start();

    }
}

class Read extends Thread{
    private Socket socket;
    private BufferedReader reader;

    public Read(Socket s){
        this.socket = s;
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
    private PrintWriter write;
    private DataOutputStream out;

    public Write(Socket s){
        this.socket = s;
        try{
            out = new DataOutputStream(socket.getOutputStream());
            write = new PrintWriter(new OutputStreamWriter(out));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        Scanner n = new Scanner(System.in);
        boolean isCreating = false;

        System.out.print("Enter your name: ");
        String name = n.nextLine();
        try {
            out.writeBytes(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Create or join room?: ");
        String answer = n.nextLine();
        if(answer.equals("create")){
            isCreating = true;
        }
        try {
            out.writeBytes(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!isCreating) {
            System.out.print("Enter a room code: ");
            String code = n.nextLine();
            try {
                out.writeBytes(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String input = "";
        while(true){
            input = n.nextLine();
            try {
                out.writeBytes(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}