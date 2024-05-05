package main;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game implements Runnable{
    private String name;
    private List<Player> players;
    private int roundNumber;
    private boolean gameEnded;
    private static final int MAX_PLAYERS = 6;

    public Game(String name) {
        this.name = name;
        this.players = Collections.synchronizedList(new ArrayList<>());;
        this.roundNumber = 1;
        this.gameEnded = false;
    }

    public void notifyPlayers(Player player) {
        for (Player p : players) {
            p.sendMessage("NOTIFY: "+player.getName()+ " has Joined the game");
        }
    }

    public void addPlayer(Player player) {

        if (players.contains(player)) {
           player.sendMessage("you already joined");
        } else {
            if (players.size() < MAX_PLAYERS) {
                notifyPlayers(player);
                players.add(player);
            } else {
                player.sendMessage("The game is full.");
            }
        }
    }

    private void handleLoser(Player player) {
        if (player.getPoints() > 1) {
            player.setPoints(player.getPoints() - 1);
        } else {
            players.remove(player);
        }
    }

    public void concludeRound() {
        String names =" ";
        String scores = " ";
        for (Player player : players) {
            names += player.getName() + ",";
            scores += player.getPoints() + ",";
        }
        for (Player player : players) {
            player.sendMessage("round " + getName() + this.roundNumber++ + names + scores);
        }
    }

    public double calculateAvgGuess() {
        int sum = 0;
        for (Player player : players) {
            sum += player.getGuess();
        }
        return (sum / players.size()) * (2.0 / 3.0);
    }

    public Player determineWinner(double avgGuess) {
        Player closestPlayer = null;
        double minDifference = Double.MAX_VALUE;

        for (Player player : players) {
            double difference = Math.abs(avgGuess - player.getGuess());
            if (difference < minDifference) {
                minDifference = difference;
                closestPlayer = player;
            }
        }
        return closestPlayer;
    }


    public void startGame() {
        // Wait for all players to be ready
        for (Player p : players) {
            while (p.ready==false);
        }

        // Send start message to each player
        for (Player p : players) {
            p.setPoints(5);
            p.sendMessage("The game has started. Enter your guess: ");
        }

    }



    public void startRound() {
        // Wait for all players to enter their guesses
        boolean allGuessed = false;
        while (!allGuessed) {
            allGuessed = true;
            for (Player player : players) {
                if (player.getGuess() == -1) {
                    allGuessed = false;
                    break;
                }
            }
            // Sleep for a short while to avoid busy-waiting
            try {
                Thread.sleep(1000); // Adjust as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calculate average guess and determine the winner
        double avgGuess = calculateAvgGuess();
        Player winner = determineWinner(avgGuess);

        // Handle losers and conclude round
        for (Player player : players) {
            if (player != winner) {
                handleLoser(player);
            }
        }
        concludeRound();
    }


    public List<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return this.name;
    }

    public void setGuess(Player player, int number) {
        player.setGuess(number);
    }

    public void setReady(Player player) {
        player.setReady();
    }

    public void chat(Player player, String message) {
    }

    @Override
    public void run() {


    }


//    public void run() {
//        try {
//            synchronized (players){
//                for (Player p : players){
//
//                }
//            }
//
//
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}