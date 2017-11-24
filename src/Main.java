import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.Socket;

public class Main {

    Socket socket;
    private final int SERVER_PORT = 1337;
    private final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //Let's connect this baby
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Sender messageSender = new Sender(inputStream);

    }

    //Listens for incoming messages
    public class MessageListener extends Thread {
        public MessageListener() {
        }
    }
}
