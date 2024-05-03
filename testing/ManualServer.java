package testing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import main.Player;
import main.Server;

/**
 * @author Abdulla Al-malki
 * this server is made to deal with client without worrying if the server class was fully implemented or not
 */
public class ManualServer {
    private static final int PORT = 13337;
    private ServerSocket serverSocket;

    public ManualServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("main.Server started. Waiting for clients...");
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Handle client connection
                Thread clientThread = new Thread(new Player(clientSocket));
                clientThread.start();
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
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
