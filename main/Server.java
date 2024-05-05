package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
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
    private static final List<Player> leaderBoard = Collections.synchronizedList(new ArrayList<>(5));

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static Game getGame(String gameName) {
        synchronized (gamesList) {

            for (Game game : gamesList) {
                if (game.getName().equalsIgnoreCase(gameName)) {
                    return game;
                }
            }
        }
        return null;

    }

    public void startServer() {
        System.out.println("main.Server started. Waiting for clients...");
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Handle client connection
                Player player= new Player(clientSocket);
                playersList.add(player);
                Thread clientThread = new Thread(player);
                clientThread.start();
<<<<<<< HEAD
//                for(Game g : gamesList){
//                    boolean allPlayersReady = true;
//                    for(Player p : g.getPlayers()){
//                        if (!p.ready){
//                            allPlayersReady = false;
//                            break;
//                        }
//                    }
//                    if (allPlayersReady){
//                        start(g);
//                    }
//                }

=======
>>>>>>> 645858881816c8a0f15de059067eacaec6b232f5

//                for (Player p : playersList){
//                    if(p.lastResponseTime >= (System.currentTimeMillis()+(2000))){
//                        clientThread.interrupt();
//                    }
//                }


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

    public static String getLeaderBoard(){
        StringBuilder leaderBoard = new StringBuilder();
        synchronized (Server.leaderBoard) {
            if(Server.leaderBoard.isEmpty())
                return "NO ONE SCORED YET!";
            for (int i = 0; i < Server.leaderBoard.size(); i++) {
                Player player = Server.leaderBoard.get(i);
                leaderBoard.append("#").append(i+1)
                        .append(player.getName()).append(" ")
                        .append(player.getPoints()).append(" ");
            }
        }
        return leaderBoard.toString();
    }

    public static void updateLeaderBoard(Player player){
        synchronized (Server.leaderBoard) {
            for (int i = 0; i < 5; i++) {
                if(Server.leaderBoard.get(i)==null){
                    Server.leaderBoard.add(i, player);
                    break;
                }else if(player.getPoints() > Server.leaderBoard.get(i).getPoints()) {
                    Server.leaderBoard.add(i, player);
                    Server.leaderBoard.remove(5);
                    break;
                }
            }
        }
    }
}
