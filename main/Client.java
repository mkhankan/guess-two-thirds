package main;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 13337;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(Socket socket){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            closeEverything(socket, in, out);
        }
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (printWriter != null){
                printWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void listenForMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;

                while (socket.isConnected()){
                    try {
                        msgFromServer = in.readLine();
                        System.out.println(msgFromServer);
                    }catch (IOException e){
                        closeEverything(socket,in,out);
                    }
                }
            }
        }).start();
    }

    public void sendMessage(){
//        try {
//            String message = in.readLine(); // Read server message
//            if (message.startsWith("IDENT")) {
//                // Send identification (pseudo or ticket)
//                out.println("pseudo marwan");
//                //out.println("ticket TEST1234");
//            }
//            // Handle server response to identification
//            String response = in.readLine();
//            System.out.println("main.Server response: " + response);
//
//            // Join a game
//            out.println("join MyGame"); // Example game name
//
//            // Handle server response to game join request
//            String gameResponse = in.readLine();
//            System.out.println("main.Server response to game join: " + gameResponse);

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                out.println(messageToSend);
            }
//        }catch (IOException e){
//            closeEverything(socket,in,out);
//        }
    }

    static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static void main(String[] args) throws IOException {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + PORT);

            Client client = new Client(socket);
            client.listenForMessages();
            client.sendMessage();
    }
}