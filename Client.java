import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 13337;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + PORT);
            // Implement client functionality here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
