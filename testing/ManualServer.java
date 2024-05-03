package testing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import main.Player;
import main.Server;

/**
 * @author Abdulla Al-malki
 * This server is made to deal with client without worrying if the server class was fully implemented or not
 */
public class ManualServer {
    private static final int PORT = 13337;
    private ServerSocket serverSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket clientSocket;

    public ManualServer(ServerSocket serverSocket) {
//        try {
            this.serverSocket = serverSocket;

//        }catch (IOException e){
//            closeEverything(serverSocket, bufferedReader, bufferedWriter);
//        }
    }
    public void closeEverything(ServerSocket socket,Socket clientSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
            if (clientSocket != null){
                clientSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void startServer() {
        System.out.println("main.Server started. Waiting for clients...");
        try {
            while (true) {
                this.clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                listenForMessage();
                sendMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ManualServer server = new ManualServer(serverSocket);
        server.startServer();
    }

    public void sendMessage(){
        try {
            Scanner scanner = new Scanner(System.in);
            while (clientSocket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write("Server: "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(serverSocket, clientSocket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (clientSocket.isConnected()){
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch (IOException e){
                        closeEverything(serverSocket, clientSocket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }
}
