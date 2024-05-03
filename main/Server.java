package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements ClientAPI{
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        server.startServer();
    }
    private static final int PORT = 13337;
    private ServerSocket serverSocket;

    public static final List<Player> playersList = Collections.synchronizedList(new ArrayList<>());
    public static final Map<String,String> ticketsMap = Collections.synchronizedMap(new HashMap<>());
    public static final List<Game> gamesList = Collections.synchronizedList(new ArrayList<>());

    public Server(ServerSocket serverSocket) {
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

    public static Game getGame(String gameName) {
        for (Game game : gamesList) {
            if (game.getName().equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public void ident() {

    }

    @Override
    public void ticket(String ticket) {

    }

    @Override
    public void menu(List<Player> players, List<Game> games) {

    }

    @Override
    public void list(Game game, List<Player> players) {

    }

    @Override
    public void notify(Game game, Player player) {

    }

    @Override
    public void start(Game game) {

    }

    @Override
    public void round(Game game, int number, List<Player> players, List<Integer> guesses, List<Integer> points, List<Boolean> results, List<Player> eliminated) {

    }

    @Override
    public void end(Game game, Player player) {

    }

    @Override
    public void info(String message) {

    }

    @Override
    public void error(String message) {

    }
}
