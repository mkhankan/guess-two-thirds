package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + PORT);

        Client client = new Client(socket);
        client.listenForMessages();
        client.sendMessage();
    }
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
            closeConnection(socket, in, out);
        }
    }
    public void closeConnection(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter){
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
                        closeConnection(socket,in,out);
                    }
                }
            }
        }).start();
    }

    public void sendMessage(){

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                out.println(messageToSend);
            }

    }

    static String timeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
