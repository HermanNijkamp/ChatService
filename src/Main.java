import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private InputStream inputStream;
    private OutputStream outputStream;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        //Sets up connection with the socket
        connect();

        printMenu();

    }

    private void connect() {
        try {
            String SERVER_ADDRESS = "localhost";
            int SERVER_PORT = 1337;
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to our chatserver!\nUsername:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();
        login(username, password);
        System.out.println("1. Chat starten\n2. Groepschat starten\n3. Uitloggen");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.println("Chat with whoms't'd?");
                String user = scanner.nextLine();
                startChat(user);
                break;
            case 2:
                System.out.println("Groupchat id?");
                break;
            case 3:
                System.out.println("You logged out.");
                break;
            default:
                System.out.println("YOU WOT?");
        }
    }

    private void startChat(String user) {
        System.out.println("chat started");
        Receiver messageReceiver = new Receiver(inputStream);
        Sender messageSender = new Sender(outputStream);

        messageReceiver.run();
    }

    private boolean login(String username, String password) {
        return true;
    }
}
