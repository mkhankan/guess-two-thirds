package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Player implements Runnable, ServerAPI{
    private Socket clientSocket;
    private static int seq = 0;
    private BufferedReader in;
    private PrintWriter out;
    private int points;
    private int guess;
    private Game joinedGame;

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
    private void handleRequest(String request) throws IOException {
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
                String gameName = request.substring(5);
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
                    guess(joinedGame, number);
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

    @Override
    public void pseudo(String pseudonym) {
        String ticket = generateTicket(String.valueOf(seq)); // Generate ticket
        seq++;
        synchronized (Server.ticketsMap) {
            Server.ticketsMap.put(ticket, pseudonym); // Store ticket-pseudonym pair
        }
        synchronized (Server.playersList) {
            Server.playersList.add(this); // Add player to the list of players
        }
        out.println("TICKET " + ticket); // Send ticket to client

    }

    @Override
    public boolean ticket(String ticket) {
        String pseudo;
        synchronized (Server.ticketsMap) {
            pseudo = Server.ticketsMap.get(ticket); // Get pseudonym associated with ticket
        }
        if (pseudo != null) {
            out.println("WELCOME " + pseudo); // Send welcome message to client
            return true;
        } else {
            error("Invalid ticket");
        }
        return false;

    }
    @Override
    public boolean join(String gameName) {
        // Check if the game exists
        Game game = Server.getGame(gameName);
        if (game != null && !game.getPlayers().contains(this)) {
            // Add this player to the game
            game.addPlayer(this);
            joinedGame = game;
            // Send acknowledgment to the client
            out.println("JOINED " + game.getName());
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
            out.println("JOINED " + game.getName());
            return false;
        }
    }

    @Override
    public void ready(Game game) {
        // Set player's readiness status
        game.setReady(this);
        // Send acknowledgment to the player
        out.println("You are ready for the game.");
    }

    @Override
    public void guess(Game game, int number) {
        // Set player's guess
        game.setGuess(this, number);
        // Send acknowledgment to the player
        out.println("Your guess has been recorded.");
    }
    }
