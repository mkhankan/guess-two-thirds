package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public class Player implements Runnable, ClientAPI{
    private Socket clientSocket;
    private static int seq = 0;
    private BufferedReader in;
    private PrintWriter out;
    private int points;
    private int guess;
    private Game joinedGame;
    private String name;
    private String ticket;
    public boolean ready = false;
    public long lastResponseTime;


    public Player(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            closeEverything(this.clientSocket, in, out);
        }
    }

    public void setPoints(int points){
        this.points=points;
    }

    public int getPoints(){
        return this.points;
    }
    public void setGuess(int guess){
        this.guess=guess;
    }

    public int getGuess(){
        return this.guess;
    }
    public void setName(String name){
        this.name= name;
    }

    public String getName(){
        return this.name;
    }

    public void setTicket(String ticket){
        this.ticket= ticket;
    }

    public String getTicket(){
        return this.ticket;
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ident();
        while (clientSocket.isConnected()) {
            try {
                String request = in.readLine();
                handleRequest(request);
            } catch (IOException e) {
                closeEverything(clientSocket, in, out);
                break;
            }
        }

    }
    private void handleRequest(String request) {
//        System.out.println("Received request: " + request);
        String[] parts = request.split(" ");
        String command = parts[0];

        switch (command) {
            case "pseudo":
                String pseudo = request.substring(7); // Extract pseudonym
                pseudo(pseudo); // Generate new ticket and associate it with pseudonym
                break;
            case "ticket":
                String ticket = request.substring(7); // Extract ticket
                boolean isValid = ticket(ticket); // Validate received ticket and, if valid, welcome player with pseudonym
                if (!isValid) {
                    error("Invalid ticket");
                }
                break;
            case "join":
                String gameName;
                if (parts.length < 2) {
                    gameName = "Game" + (int) (Math.random() * 1000);
                } else gameName= request.substring(5);
                join(gameName);
                break;
            case "ready":
                if (joinedGame != null) {
                    ready(joinedGame);
                } else {
                    error("You have not joined any game.");
                }
                break;
            case "guess":
                if (joinedGame != null) {
                    int number = Integer.parseInt(parts[1]); // Extract the guessed number
                    lastResponseTime= System.currentTimeMillis();
                    guess(joinedGame,number);
                } else {
                    error("You have not joined any game.");
                }
                break;
            case "chat":
                if (joinedGame != null) {
                    String message = request.substring(5); // Extract the message
                    joinedGame.chat(this, message);
                } else {
                    error("You have not joined any game.");
                }
                break;
            default:
                error("Invalid request.");
                break;
        }
    }

    @Override
    public void error(String error){
        out.println("ERROR "+error);
    }

    @Override
    public void info(String message){
        out.println("INFO "+message);
    }

    static String generateTicket(String seq) {
        byte[] hash = String.format("%32s", seq).getBytes();
        try {
            for (int i = 0; i < Math.random() * 64 + 1; ++i) {
                hash = MessageDigest.getInstance("SHA-256").digest(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HexFormat.ofDelimiter(":").formatHex(hash).toString().substring(78);
    }


    public void pseudo(String pseudonym) {
        String ticket = generateTicket(String.valueOf(seq)); // Generate ticket
        seq++;
        synchronized (Server.ticketsMap) {
            Server.ticketsMap.put(ticket, pseudonym); // Store ticket-pseudonym pair
        }
        this.name = pseudonym;
        synchronized (Server.playersList) {
            Server.playersList.add(this); // Add player to the list of players
        }
        out.println("TICKET " + ticket); // Send ticket to client
        out.println("LEADERBOARD "+Server.getLeaderBoard());
    }

    @Override
    public void ident() {
        out.println("IDENT"); // Request identification
    }

    @Override
    public boolean ticket(String ticket) {
        String pseudo;
        synchronized (Server.ticketsMap) {
            pseudo = Server.ticketsMap.get(ticket); // Get pseudonym associated with ticket
        }
        if (pseudo != null) {
            this.name = pseudo;
            out.println("WELCOME " + pseudo); // Send welcome message to client
            out.println("LEADERBOARD "+Server.getLeaderBoard());
            return true;
        } else {
            error("Invalid ticket");
        }
        return false;

    }

    @Override
    public void menu(List<Player> players, List<Game> Games) {
        StringBuilder gamesList = new StringBuilder("MENU: ");
        synchronized (Server.gamesList){
        for (Game game : Server.gamesList) {
            for (Player player : game.getPlayers()) {
                gamesList.append(player.getName()).append(",");
            }
            if (!game.getPlayers().isEmpty()) {
                gamesList.deleteCharAt(gamesList.length() - 1);
            }
            gamesList.append(" " + game.getName());
        }
        }
        out.println(gamesList);
    }

    @Override
    public void list(Game game, List<Player> players) {
        StringBuilder playerList = new StringBuilder("LIST: ");
        playerList.append(game.getName()).append(" ");
        for (Player player : players) {
            playerList.append(player.getName()).append(",");
        }
        if (!players.isEmpty()) {
            playerList.deleteCharAt(playerList.length() - 1);
        }
        out.println(playerList);
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


    public boolean join(String gameName) {
        if (this.name== null) {
            error("You must identify yourself first.");
            return false;
        }
        // Check if the game exists
        Game game = Server.getGame(gameName);
        if (game != null && !game.getPlayers().contains(this)) {
            // Add this player to the game
            game.addPlayer(this);
            joinedGame = game;
            // Send acknowledgment to the client
            out.println("JOINED " + game.getName());
            list(joinedGame, joinedGame.getPlayers());
            return true;
        } else {
            // Send error message to client if the game doesn't exist
            out.println("Game does not exist. Creating a new game...");
            // Create a new game
            joinedGame = new Game(gameName);
            synchronized (Server.gamesList){
                Server.gamesList.add(joinedGame);
            }
            // Add this player to the game
            joinedGame.addPlayer(this);
            // Add the game to the list of games
            Server.gamesList.add(game);
            // Send acknowledgment to the client
            out.println("JOINED " + joinedGame.getName());
            return false;
        }
    }


    @Override
    public boolean ready(Game game) {
        // Set player's readiness status
        game.getName();
        game.setReady(this);
        // Send acknowledgment to the player
        info("You are ready for the game.");
        game.startGame();
        return true;
    }

    @Override
    public boolean guess(Game game,int number) {
        // Set player's guess
        game.setGuess(this, number);
        // Send acknowledgment to the player
        info("Your guess has been recorded.");
        game.startRound();
        this.setGuess(-1);
        return true;
    }

    /**
     * Send message to the player
     * Used for chat
     * @param msg
     */
    public void sendMessage(String msg){
        out.println(msg);
    }

    public void setReady() {
        this.ready=true;
    }

}
