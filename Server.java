import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 13337;
    private static final Map<String, String> tickets = new HashMap<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Handle client connection
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
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

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("IDENT"); // Request identification
                String identification = in.readLine(); // Receive identification from client

                // Handle identification
                if (identification.startsWith("pseudo ")) {
                    String pseudo = identification.substring(7); // Extract pseudonym
                    String ticket = generateTicket(); // Generate ticket
                    tickets.put(ticket, pseudo); // Store ticket-pseudonym pair
                    out.println("TICKET " + ticket); // Send ticket to client
                } else if (identification.startsWith("ticket ")) {
                    String ticket = identification.substring(7); // Extract ticket
                    String pseudo = tickets.get(ticket); // Get pseudonym associated with ticket
                    if (pseudo != null) {
                        out.println("WELCOME " + pseudo); // Send welcome message to client
                    } else {
                        out.println("ERROR Invalid ticket"); // Send error message to client
                    }
                } else {
                    out.println("ERROR Invalid identification"); // Send error message to client
                }

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

                // Close connections
                // (Note: This part will be unreachable in this implementation because of the infinite loop)
                // in.close();
                // out.close();
                // clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateTicket() {
        return "TEST1234";
    }
}
