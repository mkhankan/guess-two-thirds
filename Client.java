import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 13337;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + PORT);
            // Implement client functionality here
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String message = in.readLine(); // Read server message
            if (message.startsWith("IDENT")) {
                // Send identification (pseudo or ticket)
                out.println("pseudo marwan");
                //out.println("ticket TEST1234");
            }

            // Handle server response to identification
            String response = in.readLine();
            System.out.println("Server response: " + response);

            // Join a game
            out.println("join MyGame"); // Example game name

            // Handle server response to game join request
            String gameResponse = in.readLine();
            System.out.println("Server response to game join: " + gameResponse);

            // Close connections
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
