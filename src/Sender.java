import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Sender {
    private InputStream inputStream;
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    public Sender(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
