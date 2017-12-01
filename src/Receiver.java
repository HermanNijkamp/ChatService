import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Receiver implements Runnable {
    InputStream inputStream;
    BufferedReader reader;

    public Receiver(InputStream inputStream) {
        this.inputStream = inputStream;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void run() {
        while (true) {
            getMessage();
            System.out.println("neer");
        }
    }

    public String getMessage() {
        String message = "";
        try {
            message = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}
