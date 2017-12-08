import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Receiver implements Runnable {
    private InputStream inputStream;
    private BufferedReader reader;

    public Receiver(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void run() {
        while (true) {
            System.out.println(getMessage());
        }
    }

    private String getMessage() {
        String message = "";
        try {
            message = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}
