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

    public Player(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            closeEverything(this.clientSocket, in, out);
        }
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
                handleIdent();

                handleGameJoin();

            } catch (IOException e) {
                closeEverything(clientSocket, in, out);
                break;
            }
        }

    }

    private void handleIdent() throws IOException {
        while (true) {
            out.println("IDENT"); // Request identification
            String identification = in.readLine(); // Receive identification from client

            // Handle identification
            if (identification.startsWith("pseudo ")) {
                String pseudo = identification.substring(7); // Extract pseudonym
                pseudo(pseudo); // Generate new ticket and associate it with pseudonym
                break;
            } else if (identification.startsWith("ticket ")) {
                String ticket = identification.substring(7); // Extract ticket
                boolean isValid = ticket(ticket); // Validate received ticket and, if valid, welcome player with pseudonym
                if (isValid)
                    break;
            } else {
                out.println("ERROR Invalid identification"); // Send error message to client
            }
        }
    }

    private void handleGameJoin() throws IOException {
        // Handle game joining
        while (true) {
            String request = in.readLine();
            if (request.startsWith("join ")) {
                String gameName = request.substring(5);
                // For now, let's just acknowledge the request
                out.println("JOINED " + gameName);
                break;
            } else {
                out.println("ERROR Invalid request"); // Send error message to client
            }
        }
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
            out.println("ERROR Invalid ticket"); // Send error message to client
        }
        return false;

    }

    @Override
    public void join(Game game) {

    }

    @Override
    public void ready(Game game) {

    }

    @Override
    public void guess(Game game, int number) {

    }
}