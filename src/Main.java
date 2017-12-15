import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        //Sets up connection with the socket
        connect();

        Receiver messageReceiver = new Receiver(inputStream);
        Thread receiverThread = new Thread(messageReceiver);
        receiverThread.start();
//        login();
        printMenu();
        try {
            receiverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        System.out.println("1. Chat starten\n2. Groepschat starten\n3. Uitloggen");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                chat();
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

    private void chat() {
        System.out.println("Chat Started");

        Sender messageSender = new Sender(outputStream);

        messageSender.send("HELO Menno");
        while (true) {
            String message = scanner.nextLine();
            if(message.equals("QUIT")) {
                messageSender.send(message);
            }
            messageSender.send("BCST " + message);
        }
    }

    public void groupChat() {
        System.out.println("Chat with whoms't'd?");
        String user = scanner.nextLine();
        System.out.println("Chat started with " + user);
    }

    private boolean login() {
        System.out.println("Welcome to our chatserver!\nUsername:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();
        return true;
    }
}
