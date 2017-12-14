import java.io.*;

public class Sender {

    private OutputStream outputStream;

    public Sender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(String message) {
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(message);
        writer.flush();
    }
}
