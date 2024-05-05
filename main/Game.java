package main;

import java.util.ArrayList;

public class Game {
    private String name;
    private ArrayList<Player> players;
    private int roundNumber;
    private boolean gameEnded;
    private static final int MAX_PLAYERS = 6;

    public Game(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.roundNumber = 0;
        this.gameEnded = false;
    }

    public void notifyPlayers(Player player) {
        for (Player p : players) {
            System.out.println(player.getName() + " has joined.");
        }
    }

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            System.out.println(player.getName() + " has already joined.");
        } else {
            if (players.size() < MAX_PLAYERS) {
                players.add(player);
            } else {
                System.out.println("The game is full.");
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
        System.out.println("round"+getName()+this.roundNumber+names+scores);
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
        if (players.size() < 2) {
            System.out.println("Cannot start the game. Minimum of 2 players required.");
            return;
        }

        for (Player player : players) {
            player.setPoints(5); // Set initial points for each player
            System.out.println("The game has started. Round 1 begins now. You have 5 points.");
        }

        roundNumber = 1; // Initialize round number
        gameEnded = false; // Reset game end flag

        startRound();
    }

    private void startRound() {
        double avgGuess = calculateAvgGuess();
        Player winner = determineWinner(avgGuess);

        for (Player player : players) {
            if (player != winner) {
                handleLoser(player);
            }
        }

        concludeRound();
    }

    public ArrayList<Player> getPlayers() {
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
}