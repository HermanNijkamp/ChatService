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

        Receiver messageReceiver = new Receiver(inputStream);
        Sender messageSender = new Sender(outputStream);
        messageReceiver.run();

        printMenu();

        Scanner scanner = new Scanner(System.in);
        String choise = scanner.nextLine();
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

    }
}
