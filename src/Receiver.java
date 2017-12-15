import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Receiver implements Runnable {
    private BufferedReader reader;

    public Receiver(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void run() {
        while (true) {
            try {
                String message = reader.readLine();
                String firstPart = message.substring(0, 3);
                if(!firstPart.equals("+OK")) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
