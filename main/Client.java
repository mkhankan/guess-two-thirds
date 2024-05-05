package main;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + PORT);

        Client client = new Client(socket);
        client.listenForMessages();
        client.sendMessage();
    }
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 13337;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public Client(Socket socket){
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.scanner = new Scanner(System.in);
        }catch (IOException e){
            closeConnection(socket, in, out);
        }
    }
    public void closeConnection(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (printWriter != null){
                printWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void listenForMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                while (socket.isConnected()){
                    try {
                        msgFromServer = in.readLine();
                        if (msgFromServer.toLowerCase().startsWith("ident")) {
                            handleIdent();
                        }else {
                            System.out.println(timeStamp()+" "+msgFromServer);
                        }
                        System.out.println(timeStamp()+" "+msgFromServer);
                    }catch (IOException e){
                        closeConnection(socket,in,out);
                    }
                }
            }
        }).start();
    }

    public void sendMessage(){

        while (socket.isConnected()){
            String messageToSend = scanner.nextLine();
            out.println(messageToSend);
        }

    }

    static String timeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void handleIdent(){
            if (socket.isConnected()){
                String storedTicket = getTicket();
                if (storedTicket != null) {
                    out.println("ticket " + storedTicket);
                }else {
                    String name;
                    boolean validName = true;
                    do { // name should not contain special characters
                        System.out.println("Enter your name: ");
                        name = scanner.nextLine();
                        for (char c : name.toCharArray()) {
                            if (!Character.isLetterOrDigit(c) && c != '\0') {
                                System.out.println("Name should not contain special characters");
                                validName = false;
                                break;
                            }

                        }
                    } while(!validName);


                    out.println("pseudo " + name);
                    try {
                        String response = in.readLine();
                        if (response.toLowerCase().startsWith("ticket")) {
                            String ticket = response.substring(7);
                            storeTicket(ticket);
                        }
                    } catch (IOException e) {
                        closeConnection(socket, in, out);
                    }
                }
            }
    }

    private void storeTicket(String ticket) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("ticket.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ticket);
            System.out.println("Ticket stored successfully");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getTicket(){
        try {
            FileInputStream fileInputStream = new FileInputStream("ticket.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            String ticket = (String) objectInputStream.readObject();
            System.out.println(
                    "Found local ticket"
            );
            return ticket;
        }catch (FileNotFoundException e){
            return null;
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
